package moe.fuqiuluo.remote.action.handlers

import moe.fuqiuluo.remote.action.ActionSession
import moe.fuqiuluo.remote.action.IActionHandler
import moe.protocol.service.data.MessageDetail
import moe.protocol.service.data.MessageSender
import moe.protocol.servlet.helper.MessageHelper
import moe.protocol.servlet.msg.MsgConvert
import moe.protocol.servlet.MsgSvc

internal object GetMsg: IActionHandler() {
    override suspend fun internalHandle(session: ActionSession): String {
        val hashCode = session.getIntOrNull("message_id")
            ?: session.getInt("msg_id")
        return invoke(hashCode, session.echo)
    }

    suspend operator fun invoke(msgHash: Int, echo: String = ""): String {
        val msg = MsgSvc.getMsg(msgHash).onFailure {
            return logic("Obtain msg failed, please check your msg_id.", echo)
        }.getOrThrow()
        val seq = msg.clientSeq.toInt()
        return ok(MessageDetail(
            msg.msgTime.toInt(),
            MessageHelper.obtainDetailTypeByMsgType(msg.chatType),
            msgHash,
            seq,
            MessageSender(
                msg.senderUin, msg.sendNickName, "unknown", 0, msg.senderUid
            ),
            MsgConvert.convertMsgRecordToMsgSegment(msg),
            msg.peerUin.toString()
        ), echo)
    }

    override val requiredParams: Array<String> = arrayOf("message_id")

    override val alias: Array<String> = arrayOf("get_message")

    override fun path(): String = "get_msg"
}