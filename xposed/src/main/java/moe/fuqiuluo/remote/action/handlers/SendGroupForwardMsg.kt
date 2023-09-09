package moe.fuqiuluo.remote.action.handlers

import moe.fuqiuluo.remote.action.ActionSession
import moe.fuqiuluo.remote.action.IActionHandler
import moe.protocol.servlet.msg.MsgSegment

internal object SendGroupForwardMsg: IActionHandler() {
    override suspend fun internalHandle(session: ActionSession): String {
        TODO("Not yet implemented")
    }

    operator fun invoke(segment: MsgSegment, echo: String = ""): String {
        if (segment.isEmpty()) {
            return logic("消息为空", echo)
        } else if (segment.size > 100) {
            return logic("消息数量过多", echo)
        }


        TODO()
    }

    override fun path(): String = "send_group_forward_msg"
}