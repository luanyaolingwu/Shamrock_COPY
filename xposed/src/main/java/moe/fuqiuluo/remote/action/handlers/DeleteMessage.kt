package moe.fuqiuluo.remote.action.handlers

import moe.fuqiuluo.remote.action.ActionSession
import moe.fuqiuluo.remote.action.IActionHandler
import moe.protocol.servlet.helper.MessageHelper
import moe.protocol.servlet.MsgSvc

internal object DeleteMessage: IActionHandler() {
    override suspend fun internalHandle(session: ActionSession): String {
        val hashCode = session.getString("message_id").toInt()
        return invoke(hashCode, session.echo)
    }

    suspend operator fun invoke(msgHash: Int, echo: String = ""): String {
        val msgId = MessageHelper.getMsgIdByHashCode(msgHash)
        val qid = MessageHelper.getQMsgIdByMsgId(msgId)
        MsgSvc.recallMsg(qid)
        return ok("成功", echo)
    }

    override fun path(): String = "delete_message"

    override val alias: Array<String> = arrayOf("delete_msg")

    override val requiredParams: Array<String> = arrayOf("message_id")
}