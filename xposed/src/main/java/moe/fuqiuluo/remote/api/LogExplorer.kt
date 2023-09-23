package moe.fuqiuluo.remote.api

import io.ktor.server.application.call
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import moe.fuqiuluo.xposed.helper.LogCenter
import moe.fuqiuluo.xposed.tools.getOrPost
import moe.fuqiuluo.xposed.tools.fetchOrNull

fun Routing.showLog() {
    getOrPost("/log") {
        val start = fetchOrNull("start")?.toIntOrNull() ?: 0
        val recent =fetchOrNull("recent")?.toBooleanStrictOrNull() ?: false
        val log = LogCenter.getLogLines(start, recent)
        call.respondText(log.joinToString("\n"))
    }
}