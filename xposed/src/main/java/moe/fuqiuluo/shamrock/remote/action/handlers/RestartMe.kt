package moe.fuqiuluo.shamrock.remote.action.handlers

import moe.fuqiuluo.shamrock.remote.action.ActionSession
import moe.fuqiuluo.shamrock.remote.action.IActionHandler

internal object RestartMe: IActionHandler() {
    override suspend fun internalHandle(session: ActionSession): String {
        return invoke(2000, session.echo)
    }

    operator fun invoke(duration: Int, echo: String = ""): String {
        return ok("不支持", echo)
    }

    override fun path(): String = "set_restart"
}