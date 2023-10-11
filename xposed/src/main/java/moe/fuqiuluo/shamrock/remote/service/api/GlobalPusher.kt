package moe.fuqiuluo.shamrock.remote.service.api

import moe.fuqiuluo.shamrock.remote.service.config.ShamrockConfig
import java.util.Collections

internal object GlobalPusher {
    private val list = Collections.synchronizedList(arrayListOf<BasePushServlet>())

    fun register(servlet: BasePushServlet){
        if (ShamrockConfig.isIgnoreAllEvent()) {
            return
        }
        if (!list.contains(servlet))
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



