package moe.fuqiuluo.remote.api

import moe.protocol.servlet.helper.LogicException
import io.ktor.server.application.call
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import moe.fuqiuluo.remote.action.ActionManager
import moe.fuqiuluo.remote.action.ActionSession
import moe.fuqiuluo.remote.action.handlers.*
import moe.fuqiuluo.xposed.tools.fetchOrNull
import moe.fuqiuluo.xposed.tools.fetchOrThrow
import moe.fuqiuluo.xposed.tools.getOrPost

fun Routing.userAction() {
    getOrPost("/set_group_leave") {
        val group = fetchOrThrow("group_id")
        call.respondText(LeaveTroop(group))
    }

    getOrPost("/_get_online_clients") {
        call.respondText(GetOnlineClients())
    }

    getOrPost("/_get_model_show") {
        val model = fetchOrThrow("model")
        call.respondText(GetModelShowList(model))
    }

    getOrPost("/_set_model_show") {
        val model = fetchOrThrow("model")
        val manu = fetchOrThrow("manu")
        val modelshow = fetchOrNull("modelshow")?: "Android"
        val imei = fetchOrThrow("imei")
        val show = fetchOrNull("show")?.toBooleanStrictOrNull()?: true
        call.respondText(SetModelShow(model, manu, modelshow, imei, show))
    }

    getOrPost("/get_model_show") {
        val uin = fetchOrNull("user_id")
        call.respondText(GetModelShow(uin?.toLong() ?: 0))
    }

    getOrPost("/clean_cache") {
        call.respondText(CleanCache())
    }

    getOrPost("/set_restart") {
        call.respondText(RestartMe(2000))
    }

    getOrPost("/send_like") {
        val uin = fetchOrThrow("user_id")
        val cnt = fetchOrThrow("times")
        call.respondText(ActionManager["send_like"]?.handle(ActionSession(mapOf(
            "user_id" to uin,
            "cnt" to cnt
        ))) ?: throw LogicException("Unable to obtain send_like handler."))
    }
}