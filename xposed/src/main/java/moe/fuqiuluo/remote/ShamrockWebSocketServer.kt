@file:OptIn(DelicateCoroutinesApi::class)

package moe.fuqiuluo.remote

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
import moe.fuqiuluo.xposed.tools.ifNullOrEmpty
import moe.protocol.service.WebSocketService
import moe.protocol.service.config.ShamrockConfig
import moe.protocol.service.data.BotStatus
import moe.protocol.service.data.Self
import moe.protocol.service.data.push.MetaEventType
import moe.protocol.service.data.push.MetaSubType
import moe.protocol.service.data.push.PostType
import moe.protocol.service.data.push.PushMetaEvent
import moe.protocol.servlet.helper.ErrorTokenException
import mqq.app.MobileQQ
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.net.InetSocketAddress
import java.net.URI
import java.util.Collections
import kotlin.concurrent.timer


internal var InternalWebSocketServer: ShamrockWebSocketServer? = null

internal class ShamrockWebSocketServer(
    port: Int
) : WebSocketServer(InetSocketAddress(port)) {
    private val eventReceivers = Collections.synchronizedList(mutableListOf<WebSocket>())

    fun initHeartbeat() {
        timer("heartbeat", true, 0, 1000L * 5) {
            if (InternalWebSocketServer == null) {
                this.cancel()
            }
            val runtime = MobileQQ.getMobileQQ().waitAppRuntime()
            val curUin = runtime.currentAccountUin
            broadcastAnyEvent(PushMetaEvent(
                time = System.currentTimeMillis() / 1000,
                selfId = WebSocketService.app.longAccountUin,
                postType = PostType.Meta,
                type = MetaEventType.Heartbeat,
                subType = MetaSubType.Connect,
                status = BotStatus(
                    Self("qq", curUin), runtime.isLogin, status = "正常", good = true
                ),
                interval = 15000
            ))
        }
    }

    inline fun <reified T> broadcastAnyEvent(any: T) {
        broadcastTextEvent(GlobalJson.encodeToString(any))
    }

    fun broadcastTextEvent(text: String) {
        broadcast(text, eventReceivers)
    }

    override fun onOpen(conn: WebSocket, handshake: ClientHandshake) {
        val token = ShamrockConfig.getToken()
        if (token.isNotBlank()) {
            var accessToken = handshake.getFieldValue("access_token")
                .ifNullOrEmpty(handshake.getFieldValue("ticket"))
                .ifNullOrEmpty(handshake.getFieldValue("Authorization"))
                ?: throw ErrorTokenException
            if (accessToken.startsWith("Bearer ")) {
                accessToken = accessToken.substring(7)
            }
            if (token != accessToken) {
                conn.close()
                LogCenter.log({ "WSServer连接错误(${conn.remoteSocketAddress.address.hostAddress}:${conn.remoteSocketAddress.port}) 没有提供正确的token, $accessToken。" }, Level.ERROR)
                return
            }
        }
        val path = URI.create(handshake.resourceDescriptor).path
        if (path != "/api") {
            WebSocketService.pushMetaLifecycle()
            eventReceivers.add(conn)
        }
        LogCenter.log({ "WSServer连接(${conn.remoteSocketAddress.address.hostAddress}:${conn.remoteSocketAddress.port}$path)" }, Level.DEBUG)
    }

    override fun onClose(conn: WebSocket, code: Int, reason: String, remote: Boolean) {
        val path = URI.create(conn.resourceDescriptor).path
        if (path != "/api") {
            eventReceivers.remove(conn)
        }
        LogCenter.log({ "WSServer断开(${conn.remoteSocketAddress.address.hostAddress}:${conn.remoteSocketAddress.port}$path): $code,$reason,$remote" }, Level.DEBUG)
    }

    override fun onMessage(conn: WebSocket, message: String) {
        val path = URI.create(conn.resourceDescriptor).path
        GlobalScope.launch {
            onHandleAction(conn, message, path)
        }
    }

    private suspend fun onHandleAction(conn: WebSocket, message: String, path: String) {
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
        respond?.let { conn.send(it) }
    }

    override fun onError(conn: WebSocket, ex: Exception?) {
        LogCenter.log("WSServer Error: " + ex?.stackTraceToString(), Level.ERROR)
    }

    override fun onStart() {
        LogCenter.log("WSServer start running on ws://0.0.0.0:$port!")
    }
}