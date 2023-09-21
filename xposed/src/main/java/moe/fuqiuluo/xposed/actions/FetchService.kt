@file:OptIn(DelicateCoroutinesApi::class)
package moe.fuqiuluo.xposed.actions

import android.content.Context
import com.tencent.qqnt.kernel.api.IKernelService
import com.tencent.qqnt.kernel.api.impl.KernelServiceImpl
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import moe.fuqiuluo.xposed.helper.Level
import moe.fuqiuluo.xposed.helper.LogCenter
import moe.fuqiuluo.xposed.helper.NTServiceFetcher
import moe.fuqiuluo.xposed.loader.NativeLoader
import moe.fuqiuluo.xposed.tools.hookMethod
import moe.protocol.servlet.utils.PlatformUtils

internal class FetchService: IAction {
    override fun invoke(ctx: Context) {
        NativeLoader.load("shamrock")

        if (PlatformUtils.isMqq()) {
            KernelServiceImpl::class.java.hookMethod("initService").after {
                val service = it.thisObject as IKernelService
                LogCenter.log("NTKernel try to init service: $service", Level.DEBUG)
                GlobalScope.launch {
                    NTServiceFetcher.onFetch(service)
                }
            }
        } else {
            // TIM 尚未进入 NTKernel
            LogCenter.log("NTKernel try to init service: not in mqq process", Level.ERROR)
        }

    }
}