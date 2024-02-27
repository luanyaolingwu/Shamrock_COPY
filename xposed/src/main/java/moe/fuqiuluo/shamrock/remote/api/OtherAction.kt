package moe.fuqiuluo.shamrock.remote.api

import io.ktor.http.ContentType
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.server.application.call
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.json.JsonObject
import moe.fuqiuluo.shamrock.remote.action.handlers.CleanCache
import moe.fuqiuluo.shamrock.remote.action.handlers.DownloadFile
import moe.fuqiuluo.shamrock.remote.action.handlers.GetDeviceBattery
import moe.fuqiuluo.shamrock.remote.action.handlers.GetVersionInfo
import moe.fuqiuluo.shamrock.remote.action.handlers.RestartMe
import moe.fuqiuluo.shamrock.remote.action.handlers.UploadFileToShamrock
import moe.fuqiuluo.shamrock.remote.structures.Status
import moe.fuqiuluo.shamrock.remote.service.config.ShamrockConfig
import moe.fuqiuluo.shamrock.tools.asString
import moe.fuqiuluo.shamrock.tools.fetchOrNull
import moe.fuqiuluo.shamrock.tools.fetchOrThrow
import moe.fuqiuluo.shamrock.tools.fetchPostJsonArray
import moe.fuqiuluo.shamrock.tools.getOrPost
import moe.fuqiuluo.shamrock.tools.hex2ByteArray
import moe.fuqiuluo.shamrock.tools.isJsonArray
import moe.fuqiuluo.shamrock.tools.json
import moe.fuqiuluo.shamrock.tools.respond
import moe.fuqiuluo.shamrock.utils.FileUtils
import moe.fuqiuluo.shamrock.utils.MD5
import java.io.File

fun Routing.otherAction() {

    if (ShamrockConfig.allowShell()) {
        post("/shell") {
            val runtime = Runtime.getRuntime()
            val dir = fetchOrThrow("dir")
            val out = hashMapOf<String, Any>()
            withTimeoutOrNull(5000L) {
                if (isJsonArray("cmd")) {
                    val cmd = fetchPostJsonArray("cmd").map {
                        if (it is JsonObject) it.toString() else it.asString
                    }.toTypedArray()
                    withContext(Dispatchers.IO) {
                        runtime.exec(cmd, null, File(dir)).apply { waitFor() }
                    }
                } else {
                    val cmd = fetchOrThrow("cmd")
                    withContext(Dispatchers.IO) {
                        runtime.exec(cmd, null, File(dir)).apply { waitFor() }
                    }
                }
            }.also {
                if (it == null) {
                    respond(false, Status.IAmTired, "执行超时")
                } else {
                    it.inputStream.use {
                        out["out"] = it.readBytes().toString(Charsets.UTF_8)
                    }
                    it.errorStream.use {
                        out["err"] = it.readBytes().toString(Charsets.UTF_8)
                    }
                }
            }

            call.respondText(out.json.toString(), ContentType.Application.Json)
        }
    }

    getOrPost("/get_version_info") {
        call.respondText(GetVersionInfo(), ContentType.Application.Json)
    }

    getOrPost("/get_device_battery") {
        call.respondText(GetDeviceBattery(), ContentType.Application.Json)
    }

    getOrPost("/clean_cache") {
        call.respondText(CleanCache(), ContentType.Application.Json)
    }

    getOrPost("/set_restart") {
        call.respondText(RestartMe(2000), ContentType.Application.Json)
    }

    getOrPost("/download_file") {
        val url = fetchOrNull("url")
        val b64 = fetchOrNull("base64")
        val name = fetchOrNull("name")
        val root = fetchOrNull("root")
        val threadCnt = fetchOrNull("thread_cnt")?.toInt() ?: 0
        val headers = fetchOrNull("headers") ?: ""
        call.respondText(DownloadFile(url, b64, threadCnt, headers.split("\r\n"), name, root), ContentType.Application.Json)
    }

    post("/upload_file") {
        val partData = call.receiveMultipart()
        partData.forEachPart { part ->
            if (part.name == "file") {
                val bytes = (part as PartData.FileItem).streamProvider().readBytes()
                val tmp = FileUtils.renameByMd5(FileUtils.getTmpFile("cache").also {
                    it.writeBytes(bytes)
                })
                respond(true, Status.Ok, DownloadFile.DownloadResult(
                    file = tmp.absolutePath,
                    md5 = MD5.genFileMd5Hex(tmp.absolutePath)
                ), "成功")
                return@forEachPart
            }
        }
        respond(false, Status.BadRequest, "没有上传文件信息")
    }

    getOrPost("/upload_file_to_shamrock") {
        val md5 = fetchOrThrow("md5").hex2ByteArray()
        val offset = fetchOrNull("offset")?.toULong() ?: 0uL
        val chunk = fetchOrThrow("chunk").toByteArray()
        val fileSize = fetchOrNull("file_size")?.toULong() ?: chunk.size.toULong()
        call.respondText(UploadFileToShamrock(md5, fileSize, offset, chunk), ContentType.Application.Json)
    }

    getOrPost("/config/set_boolean") {
        val key = fetchOrThrow("key")
        val value = fetchOrThrow("value").toBooleanStrict()
        ShamrockConfig[key] = value
        respond(true, Status.Ok, "success")
    }

    getOrPost("/config/set_int") {
        val key = fetchOrThrow("key")
        val value = fetchOrThrow("value").toInt()
        ShamrockConfig[key] = value
        respond(true, Status.Ok, "success")
    }

    getOrPost("/config/set_string") {
        val key = fetchOrThrow("key")
        val value = fetchOrThrow("value")
        ShamrockConfig[key] = value
        respond(true, Status.Ok, "success")
    }
}