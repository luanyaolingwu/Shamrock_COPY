package moe.fuqiuluo.shamrock.remote.service.api

import moe.fuqiuluo.shamrock.remote.service.config.ShamrockConfig
import java.util.Collections

internal object GlobalPusher {
    private val cacheConn = Collections.synchronizedMap(mutableMapOf<String, BasePushServlet>())

    fun register(servlet: BasePushServlet){
        if (ShamrockConfig.isIgnoreAllEvent()) {
            return
        }
        if (!cacheConn.containsKey(servlet.id) && !cacheConn.containsValue(servlet))
            cacheConn[servlet.id] = servlet
    }

    fun unregister(servlet: BasePushServlet){
        if (cacheConn.containsKey(servlet.id) || cacheConn.containsValue(servlet))
            cacheConn.remove(servlet.id)
    }

    operator fun invoke(): List<BasePushServlet> {
        return cacheConn.map { it.value }
    }
}



