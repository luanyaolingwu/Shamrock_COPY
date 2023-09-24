package moe.fuqiuluo.remote.api

import com.tencent.mobileqq.app.QQAppInterface
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import moe.fuqiuluo.remote.action.ActionManager
import moe.fuqiuluo.remote.action.ActionSession
import moe.fuqiuluo.remote.entries.CommonResult
import moe.fuqiuluo.remote.entries.CurrentAccount
import moe.fuqiuluo.remote.entries.Status
import moe.fuqiuluo.remote.entries.StdAccount
import moe.fuqiuluo.xposed.tools.fetchOrNull
import moe.fuqiuluo.xposed.tools.fetchOrThrow
import moe.fuqiuluo.xposed.tools.getOrPost
import moe.fuqiuluo.xposed.tools.respond
import mqq.app.MobileQQ

fun Routing.profileRouter() {
    getOrPost("/set_qq_profile") {
        val nickName = fetchOrThrow("nickname")
        val company = fetchOrThrow("company")
        val email = fetchOrThrow("email")
        val college = fetchOrThrow("college")
        val personalNote = fetchOrThrow("personal_note")

        val age = fetchOrNull("age")
        val birthday = fetchOrNull("birthday")

        val handler = ActionManager["set_qq_profile"]!!

        call.respondText(handler.handle(
            ActionSession(mapOf(
            "nickname" to nickName,
            "company" to company,
            "email" to email,
            "college" to college,
            "personal_note" to personalNote,
            "age" to age,
            "birthday" to birthday
        ))
        ))
    }

    getOrPost("/get_account_info") {
        val accounts = MobileQQ.getMobileQQ().allAccounts
        val runtime = MobileQQ.getMobileQQ().waitAppRuntime()
        val curUin = runtime.currentAccountUin
        val account = accounts?.firstOrNull { it.uin == curUin }
        if (!runtime.isLogin || account == null || !account.isLogined) {
            this.call.respond(
                CommonResult("ok", Status.InternalHandlerError.code, CurrentAccount(
                1094950020L, false, "未登录"
            )
                )
            )
        } else {
            this.call.respond(
                CommonResult("ok", 0, CurrentAccount(
                curUin.toLong(), runtime.isLogin, if (runtime is QQAppInterface) runtime.currentNickname else "unknown"
            )
                )
            )
        }
    }

    getOrPost("/get_history_account_info") {
        val accounts = MobileQQ.getMobileQQ().allAccounts
        val accountList = accounts.map {
            CurrentAccount(it.uin.toLong(), it.isLogined)
        }
        respond(true, Status.Ok, accountList)
    }

    getOrPost("/get_login_info") {
        val accounts = MobileQQ.getMobileQQ().allAccounts
        val runtime = MobileQQ.getMobileQQ().waitAppRuntime()
        val curUin = runtime.currentAccountUin
        val account = accounts.firstOrNull { it.uin == curUin }
        if (account == null || !account.isLogined) {
            respond(false, Status.BadParam, msg = "当前不处于已登录状态")
        } else {
            respond(true, Status.Ok, StdAccount(
                curUin.toLong(),if (runtime is QQAppInterface) runtime.currentNickname else "unknown"
            )
            )
        }
    }
}