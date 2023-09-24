package moe.fuqiuluo.remote.api

import com.tencent.mobileqq.app.QQAppInterface
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import kotlinx.coroutines.delay
import moe.fuqiuluo.remote.HTTPServer
import moe.fuqiuluo.remote.entries.CommonResult
import moe.fuqiuluo.remote.entries.CurrentAccount
import moe.fuqiuluo.remote.entries.Status
import moe.fuqiuluo.remote.entries.StdAccount
import moe.fuqiuluo.xposed.helper.LogCenter
import moe.fuqiuluo.xposed.tools.getOrPost
import moe.fuqiuluo.xposed.tools.respond
import mqq.app.MobileQQ
import kotlin.system.exitProcess

fun Routing.obtainFrameworkInfo() {
    getOrPost("/get_start_time") {
        respond(
            isOk = true,
            code = Status.Ok,
            HTTPServer.startTime
        )
    }

    get("/shut") {
        HTTPServer.stop()
        LogCenter.log("正在关闭Shamrock。", toast = true)
        delay(3000)
        exitProcess(0)
    }
}
