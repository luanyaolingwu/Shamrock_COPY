package moe.fuqiuluo.remote.api

import moe.protocol.servlet.TicketSvc
import io.ktor.server.application.call
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import moe.fuqiuluo.remote.action.handlers.GetCSRF
import moe.fuqiuluo.remote.action.handlers.GetCookies
import moe.fuqiuluo.remote.action.handlers.GetCredentials
import moe.fuqiuluo.remote.entries.Status
import moe.fuqiuluo.xposed.tools.EmptyJsonObject
import moe.fuqiuluo.xposed.tools.asJsonObject
import moe.fuqiuluo.xposed.tools.fetchOrNull
import moe.fuqiuluo.xposed.tools.fetchOrThrow
import moe.fuqiuluo.xposed.tools.getOrPost
import moe.fuqiuluo.xposed.tools.json
import moe.fuqiuluo.xposed.tools.respond
import moe.fuqiuluo.xposed.tools.toHexString

fun Routing.ticketActions() {
    getOrPost("/get_credentials") {
        val domain = fetchOrNull("domain")
        if (domain != null) {
            call.respondText(GetCredentials(domain))
        } else {
            call.respondText(GetCredentials())
        }
    }

    getOrPost("/get_cookies") {
        val domain = fetchOrNull("domain")
        if (domain != null) {
            call.respondText(GetCookies(domain = domain))
        } else {
            call.respondText(GetCookies())
        }
    }

    getOrPost("/get_csrf_token") {
        val domain = fetchOrNull("domain")
        if (domain != null) {
            call.respondText(GetCSRF(domain))
        } else {
            call.respondText(GetCSRF())
        }
    }

    getOrPost("/get_ticket") {
        val uin = fetchOrThrow("uin")
        val ticket = when(val id = fetchOrThrow("id").toInt()) {
            32 -> TicketSvc.getStWeb(uin)
            else -> {
                respond(true, Status.Ok, data = TicketSvc.getTicket(uin, id)?.let {
                    mapOf(
                        "sig" to (it._sig?.toHexString() ?: "null"),
                        "key" to (it._sig_key?.toHexString() ?: "null")
                    ).json.asJsonObject
                } ?: EmptyJsonObject)
                return@getOrPost
            }
        }
        respond(true, Status.Ok, data = ticket)
    }
}