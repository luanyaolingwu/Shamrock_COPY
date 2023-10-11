package moe.fuqiuluo.shamrock.xposed.actions

import android.content.Context
import android.os.Bundle
import moe.fuqiuluo.shamrock.utils.PlatformUtils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import moe.fuqiuluo.shamrock.tools.broadcast
import moe.fuqiuluo.shamrock.helper.Level
import moe.fuqiuluo.shamrock.helper.LogCenter
import moe.fuqiuluo.shamrock.xposed.helper.internal.*
import moe.fuqiuluo.shamrock.xposed.ipc.ShamrockIpc

internal class IpcService: IAction {
    override fun invoke(ctx: Context) {
        if (!PlatformUtils.isMsfProcess()) return
        DynamicReceiver.register("fetch_ipc", IPCRequest {
            val name = it.getStringExtra("ipc_name")
            GlobalScope.launch {
                ShamrockIpc.get(name)?.also { binder ->
                    ctx.broadcast("xqbot") {
                        putExtra("__cmd", "ipc_callback")
                        putExtra("ipc", Bundle().also {
                            it.putString("name", name)
                            it.putBinder("binder", binder)
                        })
                    }
                } ?: LogCenter.log("无法获取IPC: $name", Level.WARN)
            }
        })
    }
}
