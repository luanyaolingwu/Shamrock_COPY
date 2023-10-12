package moe.fuqiuluo.shamrock.remote.action.handlers

import kotlinx.serialization.Serializable
import moe.fuqiuluo.shamrock.remote.action.ActionSession
import moe.fuqiuluo.shamrock.remote.action.IActionHandler
import moe.fuqiuluo.shamrock.tools.asString
import moe.fuqiuluo.shamrock.utils.DownloadUtils
import moe.fuqiuluo.shamrock.utils.FileUtils

internal object DownloadFile: IActionHandler() {
    override suspend fun internalHandle(session: ActionSession): String {
        val url = session.getString("url")
        val threadCnt = session.getIntOrNull("thread_cnt") ?: 3
        val headers = if (session.isArray("headers")) {
            session.getArray("headers").map {
                it.asString
            }
        } else {
            session.getString("headers").split("\r\n")
        }
        return invoke(url, threadCnt, headers, session.echo)
    }

    suspend operator fun invoke(
        url: String,
        threadCnt: Int,
        headers: List<String>,
        echo: String = ""
    ): String {
        return invoke(url, threadCnt, headers.associate {
            it.split("=").let { (k, v) ->
                k to v
            }
        }, echo)
    }

    suspend operator fun invoke(
        url: String,
        threadCnt: Int,
        headers: Map<String, String>,
        echo: String = ""
    ): String {
        var tmp = FileUtils.getTmpFile("cache")
        if(DownloadUtils.download(
            urlAdr = url,
            dest = tmp,
            headers = headers,
            threadCount = threadCnt
        )) {
            return error("下载失败", echo)
        }
        tmp = FileUtils.renameByMd5(tmp)
        return ok(data = DownloadResult(
            file = tmp.absolutePath
        ),"成功", echo)
    }

    override val requiredParams: Array<String> = arrayOf("url")

    override fun path(): String = "download_file"

    @Serializable
    data class DownloadResult(
        val file: String
    )
}