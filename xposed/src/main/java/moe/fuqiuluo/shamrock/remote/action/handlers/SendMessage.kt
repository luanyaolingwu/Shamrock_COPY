package moe.fuqiuluo.shamrock.remote.action.handlers

import com.tencent.qqnt.kernel.nativeinterface.MsgConstant
import moe.fuqiuluo.shamrock.remote.action.ActionSession
import moe.fuqiuluo.shamrock.remote.action.IActionHandler
import moe.fuqiuluo.shamrock.helper.MessageHelper
import moe.fuqiuluo.shamrock.helper.ParamsException
import moe.fuqiuluo.qqinterface.servlet.MsgSvc
import kotlinx.serialization.json.JsonArray
import moe.fuqiuluo.shamrock.remote.service.data.MessageResult
import moe.fuqiuluo.shamrock.tools.json
import moe.fuqiuluo.shamrock.helper.Level
import moe.fuqiuluo.shamrock.helper.LogCenter

internal object SendMessage: IActionHandler() {
    override suspend fun internalHandle(session: ActionSession): String {
        val detailType = session.getStringOrNull("detail_type") ?: session.getString("message_type")
        try {
            val chatType = MessageHelper.obtainMessageTypeByDetailType(detailType)
            val peerId = when(chatType) {
                MsgConstant.KCHATTYPEGROUP -> session.getStringOrNull("group_id") ?: return noParam("group_id", session.echo)
                MsgConstant.KCHATTYPEC2C -> session.getStringOrNull("user_id") ?: return noParam("user_id", session.echo)
                else -> error("unknown chat type: $chatType")
            }
            return if (session.isString("message")) {
                val autoEscape = session.getBooleanOrDefault("auto_escape", false)
                val message = session.getString("message")
                invoke(chatType, peerId, message, autoEscape, session.echo)
            } else {
                val message = session.getArray("message")
                invoke(chatType, peerId, message, session.echo)
            }
        } catch (e: ParamsException) {
            return noParam(e.message!!, session.echo)
        } catch (e: Throwable) {
            return logic(e.message ?: e.toString(), session.echo)
        }
    }

    // 发送文本格式/CQ码类型消息
    suspend operator fun invoke(
        chatType: Int,
        peerId: String,
        message: String,
        autoEscape: Boolean,
        echo: String = ""
    ): String {
        val result = if (autoEscape) {
            MsgSvc.sendToAio(chatType, peerId, arrayListOf(message).json)
        } else {
            val msg = MessageHelper.decodeCQCode(message)
            if (msg.isEmpty()) {
                LogCenter.log("CQ码不合法", Level.WARN)
                return logic("CQCode is illegal", echo)
            } else {
                MsgSvc.sendToAio(chatType, peerId, msg)
            }
        }
        return ok(
            MessageResult(
            msgId = result.second,
            time = result.first * 0.001
        ), echo)
    }

    // 消息段格式消息
    suspend operator fun invoke(
        chatType: Int, peerId: String, message: JsonArray, echo: String = ""
    ): String {
        val result = MsgSvc.sendToAio(chatType, peerId, message)
        return ok(MessageResult(
            msgId = result.second,
            time = result.first * 0.001
        ), echo)
    }

    override val requiredParams: Array<String> = arrayOf("message")

    override fun path(): String = "send_message"

    override val alias: Array<String> = arrayOf("send_msg")
}