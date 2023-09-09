@file:OptIn(DelicateCoroutinesApi::class)

package moe.fuqiuluo.xposed.helper

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import moe.protocol.servlet.utils.FileUtils
import moe.fuqiuluo.xposed.actions.toast
import moe.fuqiuluo.xposed.helper.internal.DataRequester
import moe.protocol.service.config.ShamrockConfig
import mqq.app.MobileQQ
import java.io.File
import java.util.Date

internal enum class Level(
    val id: Byte
) {
    DEBUG(0),
    INFO(1),
    WARN(2),
    ERROR(3),
}

internal object LogCenter {
    private val LogFile = FileUtils.getFile(
        dir = "log",
        name = MobileQQ.getMobileQQ().qqProcessName.replace(":", ".") + ".log"
    )
    @SuppressLint("SimpleDateFormat")
    private val format = SimpleDateFormat("[HH:mm:ss] ")

    fun log(string: String, level: Level = Level.INFO, toast: Boolean = false) =
        log({ string }, level, toast)

    fun log(
        string: () -> String,
        level: Level = Level.INFO,
        toast: Boolean = false
    ) {
        if (!ShamrockConfig.isDebug() && level == Level.DEBUG) {
            return
        }

        val log = string()
        if (toast) {
            MobileQQ.getContext().toast(log)
        }
        // 把日志广播到主进程
        GlobalScope.launch(Dispatchers.Default) {
            DataRequester.request("send_message", bodyBuilder = {
                put("string", log)
                put("level", level.id)
            })
        }

        if (!LogFile.exists()) {
            LogFile.createNewFile()
        }
        val format = "%s%s %s\n".format(format.format(Date()), level.name, log)

        LogFile.appendText(format)
    }

    fun getAllLog(): File {
        return LogFile
    }
}