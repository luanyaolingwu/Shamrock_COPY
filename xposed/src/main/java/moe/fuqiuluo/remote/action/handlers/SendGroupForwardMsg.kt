package moe.fuqiuluo.remote.action.handlers

import com.tencent.qqnt.kernel.nativeinterface.MsgRecord
import moe.fuqiuluo.remote.action.ActionSession
import moe.fuqiuluo.remote.action.IActionHandler
import moe.fuqiuluo.xposed.tools.asInt
import moe.protocol.servlet.MsgSvc
import moe.protocol.servlet.helper.MessageHelper
import moe.protocol.servlet.msg.LongMsgHelper
import moe.protocol.servlet.msg.MsgSegment

internal object SendGroupForwardMsg: IActionHandler() {
    override suspend fun internalHandle(session: ActionSession): String {
        val groupId = session.getLong("group_id")
        val hashList = session.getArrayOrNull("seqs")?.map { it.asInt }
        if (hashList != null) {
            val msgs = hashList.map {
                MessageHelper.getMsgIdByHashCode(it)
            }.map {
                MsgSvc.getMsg(it)
            }
            LongMsgHelper.uploadGroupMsg(groupId.toString(), msgs.filterNotNull())
        }

        return "xxx"
    }

    operator fun invoke(msgs: List<MsgRecord>, echo: String = ""): String {
        if (msgs.isEmpty()) {
            return logic("消息为空", echo)
        } else if (msgs.size > 100) {
            return logic("消息数量过多", echo)
        }


        TODO()
    }

    override fun path(): String = "send_group_forward_msg"
}