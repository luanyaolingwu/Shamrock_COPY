@file:OptIn(DelicateCoroutinesApi::class)

package moe.fuqiuluo.shamrock.remote.service

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import moe.fuqiuluo.shamrock.helper.ErrorTokenException
import moe.fuqiuluo.shamrock.remote.service.api.WebSocketTransmitServlet
import moe.fuqiuluo.shamrock.remote.service.config.ShamrockConfig
import moe.fuqiuluo.shamrock.remote.service.data.BotStatus
import moe.fuqiuluo.shamrock.remote.service.data.Self
import moe.fuqiuluo.shamrock.remote.service.data.push.*
import moe.fuqiuluo.shamrock.tools.ifNullOrEmpty
import moe.fuqiuluo.shamrock.helper.Level
import moe.fuqiuluo.shamrock.helper.LogCenter
import moe.fuqiuluo.shamrock.remote.service.api.GlobalEventTransmitter.onMessageEvent
import moe.fuqiuluo.shamrock.remote.service.api.GlobalEventTransmitter.onNoticeEvent
import moe.fuqiuluo.shamrock.remote.service.api.GlobalEventTransmitter.onRequestEvent
import moe.fuqiuluo.shamrock.xposed.helper.AppRuntimeFetcher
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import java.net.URI

internal class WebSocketService(
    host: String,
    port: Int,
    heartbeatInterval: Long,
): WebSocketTransmitServlet(host, port, heartbeatInterval) {
    private val subscribes = mutableSetOf<Job>()

    override fun subscribe(job: Job) {
        subscribes.add(job)
    }

    override fun init() {
        subscribe(launch {
            onMessageEvent { (_, event) -> pushTo(event) }
        })
        subscribe(launch {
            onNoticeEvent { event -> pushTo(event) }
        })
        subscribe(launch {
            onRequestEvent { event -> pushTo(event) }
        })
        LogCenter.log("WebSocketService: 初始化服务", Level.WARN)
    }

    override fun unsubscribe() {
        subscribes.removeIf { job ->
            job.cancel()
            return@removeIf true
        }
        LogCenter.log("WebSocketService: 释放服务", Level.WARN)
    }

    override fun onOpen(conn: WebSocket, handshake: ClientHandshake) {
        val token = ShamrockConfig.getActiveWebSocketConfig()?.tokens
            ?: ShamrockConfig.getActiveWebSocketConfig()?.token?.split(",", "|", "，")
            ?: listOf(ShamrockConfig.getToken())
        if (token.isNotEmpty()) {
            var accessToken = handshake.getFieldValue("access_token")
                .ifNullOrEmpty(handshake.getFieldValue("ticket"))
                .ifNullOrEmpty(handshake.getFieldValue("Authorization"))
                ?: throw ErrorTokenException
            if (accessToken.startsWith("Bearer ", ignoreCase = true)) {
                accessToken = accessToken.substring(7)
            }
            if (!token.contains(accessToken)) {
                conn.close()
                LogCenter.log({ "WSServer连接错误(${conn.remoteSocketAddress.address.hostAddress}:${conn.remoteSocketAddress.port}) 没有提供正确的token, $accessToken。" }, Level.ERROR)
                return
            }
        }
        val path = URI.create(handshake.resourceDescriptor).path
        if (path != "/api") {
            eventReceivers.add(conn)
            pushMetaLifecycle()
        }
        LogCenter.log({ "WSServer连接(${conn.remoteSocketAddress.address.hostAddress}:${conn.remoteSocketAddress.port}$path)" }, Level.WARN)
    }

    private fun pushMetaLifecycle() {
        launch {
            val runtime = AppRuntimeFetcher.appRuntime
            pushTo(PushMetaEvent(
                time = System.currentTimeMillis() / 1000,
                selfId = app.longAccountUin,
                postType = PostType.Meta,
                type = MetaEventType.LifeCycle,
                subType = MetaSubType.Connect,
                status = BotStatus(
                    Self("qq", runtime.longAccountUin), runtime.isLogin, status = "正常", good = true
                ),
                interval = heartbeatInterval
            ))
        }
    }
}