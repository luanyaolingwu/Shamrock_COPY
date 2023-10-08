package moe.fuqiuluo.remote.config

import moe.protocol.servlet.helper.ErrorTokenException
import moe.protocol.servlet.helper.LogicException
import moe.protocol.servlet.helper.ParamsException
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.uri
import io.ktor.server.response.respond
import io.ktor.util.AttributeKey
import moe.fuqiuluo.remote.entries.CommonResult
import moe.fuqiuluo.remote.entries.ErrorCatch
import moe.fuqiuluo.remote.entries.Status

fun Application.statusPages() {
    install(StatusPages) {
        exception<ParamsException> { call, cause ->
            val echo = call.attributes[AttributeKey("echo")] as? String ?: ""
            call.respond(CommonResult(
                status = "failed",
                retcode = Status.BadParam.code,
                data = ErrorCatch(call.request.uri, cause.message ?: ""),
                echo = echo
            ))
        }
        exception<LogicException> { call, cause ->
            val echo = call.attributes[AttributeKey("echo")] as? String ?: ""
            call.respond(CommonResult(
                status = "failed",
                retcode = Status.LogicError.code,
                data = ErrorCatch(call.request.uri, cause.message ?: ""),
                echo = echo
            ))
        }
        exception<ErrorTokenException> { call, cause ->
            val echo = call.attributes[AttributeKey("echo")] as? String ?: ""
            call.respond(CommonResult(
                status = "failed",
                retcode = Status.ErrorToken.code,
                data = ErrorCatch(call.request.uri, cause.message ?: ""),
                echo = echo
            ))
        }
        exception<Throwable> { call, cause ->
            val echo = call.attributes[AttributeKey("echo")] as? String ?: ""
            call.respond(CommonResult(
                status = "failed",
                retcode = Status.InternalHandlerError.code,
                data = ErrorCatch(call.request.uri, cause.message ?: ""),
                echo = echo
            ))
        }
    }
}