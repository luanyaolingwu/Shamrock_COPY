package moe.fuqiuluo.xposed.actions

import android.content.Context
import moe.fuqiuluo.xposed.tools.hookMethod
import oicq.wlogin_sdk.tools.util

internal class GuidLock: IAction {
    override fun invoke(ctx: Context) {
        val utilClass = util::class.java
        utilClass.hookMethod("needChangeGuid").before {
            it.result = false
        }
        utilClass.hookMethod("getGuidFromFile").before {
            val guid = util.get_last_guid(ctx)
            if (guid != null) {
                it.result = guid
            }
        }
        utilClass.hookMethod("saveGuidToFile").before {
            val guid = util.get_last_guid(ctx)
            if (guid != null) {
                it.result = null
            }
        }


    }
}