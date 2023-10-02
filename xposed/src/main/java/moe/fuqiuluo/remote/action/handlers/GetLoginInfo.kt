package moe.fuqiuluo.remote.action.handlers

import com.tencent.mobileqq.app.QQAppInterface
import moe.fuqiuluo.remote.action.ActionSession
import moe.fuqiuluo.remote.action.IActionHandler
import moe.fuqiuluo.remote.entries.StdAccount
import mqq.app.MobileQQ

internal object GetLoginInfo: IActionHandler() {
    override suspend fun internalHandle(session: ActionSession): String {
        return invoke(session.echo)
    }

    operator fun invoke(echo: String = ""): String {
        val accounts = MobileQQ.getMobileQQ().allAccounts
        val runtime = MobileQQ.getMobileQQ().waitAppRuntime()
        val curUin = runtime.currentAccountUin
        val account = accounts.firstOrNull { it.uin == curUin }
        return if (account == null || !account.isLogined) {
            error("当前不处于已登录状态", echo = echo)
        } else {
            ok(StdAccount(
                curUin.toLong(),if (runtime is QQAppInterface) runtime.currentNickname else "unknown"
            ), echo = echo)
        }
    }

    override fun path(): String = "get_login_info"
}