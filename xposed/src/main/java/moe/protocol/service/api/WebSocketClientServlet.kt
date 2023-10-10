@file:OptIn(DelicateCoroutinesApi::class)
package moe.protocol.service.api

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import moe.fuqiuluo.remote.action.ActionManager
import moe.fuqiuluo.remote.action.ActionSession
import moe.fuqiuluo.remote.entries.EmptyObject
import moe.fuqiuluo.remote.entries.Status
import moe.fuqiuluo.remote.entries.resultToString
import moe.fuqiuluo.xposed.helper.Level
import moe.fuqiuluo.xposed.helper.LogCenter
import moe.fuqiuluo.xposed.tools.GlobalJson
import moe.fuqiuluo.xposed.tools.asJsonObject
import moe.fuqiuluo.xposed.tools.asString
import moe.fuqiuluo.xposed.tools.asStringOrNull
import moe.protocol.service.config.ShamrockConfig
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.lang.Exception
import java.net.URI

internal abstract class WebSocketClientServlet(
    url: String,
    wsHeaders: Map<String, String>
) : BasePushServlet, WebSocketClient(URI("ws://$url"), wsHeaders) {
    override val address: String
        get() = ShamrockConfig.getWebSocketClientAddress()

    override fun allowPush(): Boolean {
        return ShamrockConfig.openWebSocketClient()
    }

    override fun onOpen(handshakedata: ServerHandshake?) {
        LogCenter.log("WebSocketClient onOpen: ${handshakedata?.httpStatus}, ${handshakedata?.httpStatusMessage}")
    }

    override fun onMessage(message: String) {
        GlobalScope.launch {
            handleMessage(message)
        }
    }

    private suspend fun handleMessage(message: String) {
        val respond = kotlin.runCatching {
            val actionObject = Json.parseToJsonElement(message).asJsonObject
            if (actionObject["post_type"].asStringOrNull == "meta_event") {
                // 防止二比把元事件push回来
                return
            }

            val action = actionObject["action"].asString
            val echo = actionObject["echo"].asStringOrNull ?: ""
            val params = actionObject["params"].asJsonObject

            val handler = ActionManager[action]
            handler?.handle(ActionSession(params, echo))
                ?: resultToString(false, Status.UnsupportedAction, EmptyObject, "不支持的Action", echo = echo)
        }.getOrNull()
        respond?.let { send(it) }
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        LogCenter.log("WebSocketClient onClose: $code, $reason, $remote")
        GlobalPusher.unregister(this)
    }

    override fun onError(ex: Exception?) {
        LogCenter.log("WebSocketClient onError: ${ex?.message}")
        GlobalPusher.unregister(this)
    }

    protected inline fun <reified T> pushTo(body: T) {
        if(!allowPush() || isClosed || isClosing) return
        try {
            send(GlobalJson.encodeToString(body))
        } catch (e: Throwable) {
            LogCenter.log("被动WS推送失败: ${e.stackTraceToString()}", Level.ERROR)
        }
    }
}