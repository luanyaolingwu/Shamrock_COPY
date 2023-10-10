@file:OptIn(DelicateCoroutinesApi::class)
package moe.fuqiuluo.xposed.actions

import android.content.Context
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import moe.fuqiuluo.remote.HTTPServer
import moe.fuqiuluo.xposed.helper.Level
import moe.fuqiuluo.xposed.helper.LogCenter
import moe.protocol.service.HttpService
import moe.protocol.service.WebSocketClientService
import moe.protocol.service.WebSocketService
import moe.protocol.service.api.GlobalPusher
import moe.protocol.service.config.ShamrockConfig
import moe.protocol.servlet.utils.PlatformUtils
import mqq.app.MobileQQ
import kotlin.concurrent.timer

internal class InitRemoteService: IAction {
    override fun invoke(ctx: Context) {
        if (!PlatformUtils.isMainProcess()) return

        GlobalScope.launch {
            try {
                HTTPServer.start(ShamrockConfig.getPort())
            } catch (e: Throwable) {
                LogCenter.log(e.stackTraceToString(), Level.ERROR)
            }
        }

        if (ShamrockConfig.allowWebHook()) {
            GlobalPusher.register(HttpService)
        }

        if (ShamrockConfig.openWebSocket()) {
            startWebSocketServer()
        }

        if (ShamrockConfig.openWebSocketClient()) {
            ShamrockConfig.getWebSocketClientAddress().split(",", "|", "，").forEach {  url ->
                startWebSocketClient(url)
            }
        }
    }

    private fun startWebSocketServer() {
        GlobalScope.launch {
            try {
                val server = WebSocketService(ShamrockConfig.getWebSocketPort())
                server.start()
                GlobalPusher.register(server)
            } catch (e: Throwable) {
                LogCenter.log(e.stackTraceToString(), Level.ERROR)
            }
        }
    }

    private fun startWebSocketClient(url: String) {
        GlobalScope.launch {
            try {
                val runtime = MobileQQ.getMobileQQ().waitAppRuntime()
                val curUin = runtime.currentAccountUin
                val wsHeaders = hashMapOf(
                    "X-Self-ID" to curUin
                )
                val token = ShamrockConfig.getToken()
                if (token.isNotBlank()) {
                    wsHeaders["authorization"] = "bearer $token"
                    //wsHeaders["bearer"] = token
                }

                var wsClient = WebSocketClientService(url, wsHeaders)
                wsClient.connect()
                timer(initialDelay = 5000L, period = 5000L) {
                    if (wsClient.isClosed || wsClient.isClosing) {
                        GlobalPusher.unregister(wsClient)
                        wsClient = WebSocketClientService(url, wsHeaders)
                        wsClient.connect()
                    }
                    GlobalPusher.register(wsClient)
                }
            } catch (e: Throwable) {
                LogCenter.log(e.stackTraceToString(), Level.ERROR)
            }
        }
    }
}