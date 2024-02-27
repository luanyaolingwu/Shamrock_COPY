package moe.fuqiuluo.shamrock.remote.action.handlers

import android.util.Base64
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import moe.fuqiuluo.shamrock.remote.action.ActionSession
import moe.fuqiuluo.shamrock.remote.action.IActionHandler
import moe.fuqiuluo.shamrock.tools.EmptyJsonObject
import moe.fuqiuluo.shamrock.tools.EmptyJsonString
import moe.fuqiuluo.shamrock.tools.asString
import moe.fuqiuluo.shamrock.utils.DownloadUtils
import moe.fuqiuluo.shamrock.utils.FileUtils
import moe.fuqiuluo.shamrock.utils.MD5
import moe.fuqiuluo.symbols.OneBotHandler
import java.io.File

@OneBotHandler("download_file")
internal object DownloadFile: IActionHandler() {
    override suspend fun internalHandle(session: ActionSession): String {
        val url = session.getStringOrNull("url")
        val name = session.getStringOrNull("name")
        val b64 = session.getStringOrNull("base64")
        val rootDir = session.getStringOrNull("root")
        val threadCnt = session.getIntOrNull("thread_cnt") ?: 3
        val headers = if (session.has("headers")) (if (session.isArray("headers")) {
            session.getArray("headers").map {
                it.asString
            }
        } else {
            session.getString("headers").split("\r\n")
        }) else emptyList()
        return invoke(url, b64, threadCnt, headers, name, rootDir, session.echo)
    }

    suspend operator fun invoke(
        url: String?,
        base64: String?,
        threadCnt: Int,
        headers: List<String>,
        name: String?,
        root: String?,
        echo: JsonElement = EmptyJsonString
    ): String {
        if (url != null) {
            val headerMap = mutableMapOf(
                "User-Agent" to "Shamrock"
            )
            headers.forEach {
                val pair = it.split("=")
                if (pair.size >= 2) {
                    val (k, v) = pair
                    headerMap[k] = v
                }
            }
            return invoke(url, threadCnt, headerMap, name, root, echo)
        } else if (base64 != null) {
            return invoke(base64, name, root, echo)
        } else {
            return noParam("url/base64", echo)
        }
    }

    operator fun invoke(
        base64: String,
        name: String?,
        root: String?,
        echo: JsonElement
    ): String {
        kotlin.runCatching {
            val bytes = Base64.decode(base64, Base64.DEFAULT)
            FileUtils.getTmpFile("cache").also {
                it.writeBytes(bytes)
            }
        }.onSuccess {
            var tmp = if (name == null)
                FileUtils.renameByMd5(it)
            else it.parentFile!!.resolve(name).also { target ->
                it.renameTo(target)
                it.delete()
            }
            if (root != null) {
                tmp = File(root).resolve(name ?: tmp.name).also {
                    tmp.renameTo(it)
                }
            }
            return ok(data = DownloadResult(
                file = tmp.absolutePath,
                md5 = MD5.genFileMd5Hex(tmp.absolutePath)
            ), msg = "成功", echo = echo)
        }.onFailure {
            return logic("Base64格式错误", echo)
        }
        return logic("未知错误", echo)
    }

    suspend operator fun invoke(
        url: String,
        threadCnt: Int,
        headers: Map<String, String>,
        name: String?,
        root: String?,
        echo: JsonElement = EmptyJsonString
    ): String {
        return kotlin.runCatching {
            var tmp = FileUtils.getTmpFile("cache")
            if(!DownloadUtils.download(
                    urlAdr = url,
                    dest = tmp,
                    headers = headers,
                    threadCount = threadCnt
                )) {
                return error("下载失败 (0x1)", echo)
            }
            tmp = if (name == null) {
                FileUtils.renameByMd5(tmp)
            } else {
                val newFile = tmp.parentFile!!.resolve(name)
                tmp.renameTo(newFile)
                newFile
            }
            if (root != null) {
                tmp = File(root).resolve(name ?: tmp.name).also {
                    tmp.renameTo(it)
                }
            }
            ok(data = DownloadResult(
                file = tmp.absolutePath,
                md5 = MD5.genFileMd5Hex(tmp.absolutePath)
            ), msg = "成功", echo = echo)
        }.getOrElse {
            logic(it.stackTraceToString(), echo)
        }
    }

    @Serializable
    data class DownloadResult(
        val file: String,
        val md5: String
    )
}