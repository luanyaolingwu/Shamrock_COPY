package moe.protocol.service.api

import moe.protocol.service.config.ShamrockConfig
import java.util.Collections

internal object GlobalPusher {
    private val list = Collections.synchronizedList(arrayListOf<BasePushServlet>())

    fun register(servlet: BasePushServlet){
        if (ShamrockConfig.isIgnoreAllEvent()) {
            return
        }
        list.add(servlet)
    }

    fun unregister(servlet: BasePushServlet){
        if (list.contains(servlet))
            list.remove(servlet)
    }

    operator fun invoke(): List<BasePushServlet> {
        return list
    }
}



