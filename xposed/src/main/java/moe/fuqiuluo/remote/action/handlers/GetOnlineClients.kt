package moe.fuqiuluo.remote.action.handlers

import moe.fuqiuluo.remote.action.ActionSession
import moe.fuqiuluo.remote.action.IActionHandler

internal object GetOnlineClients: IActionHandler() {
    override suspend fun internalHandle(session: ActionSession): String {
        return invoke(session.echo)
    }

    suspend operator fun invoke(echo: String = ""): String {


        return ""
    }

    override fun path(): String = "_get_online_clients"
}