package moe.fuqiuluo.shamrock.remote.api

import io.ktor.server.application.call
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import moe.fuqiuluo.shamrock.remote.action.handlers.GetDeviceBattery
import moe.fuqiuluo.shamrock.tools.getOrPost

fun Routing.otherAction() {
    getOrPost("/get_device_battery") {
        call.respondText(GetDeviceBattery())
    }

}