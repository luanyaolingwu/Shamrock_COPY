package moe.fuqiuluo.shamrock.remote.api

import moe.fuqiuluo.shamrock.utils.FileUtils
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.document
import io.ktor.server.response.respond
import io.ktor.server.response.respondFile
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import moe.fuqiuluo.shamrock.remote.action.handlers.*
import moe.fuqiuluo.shamrock.tools.*

private fun formatFileName(file: String): String = file
    .replace(regex = "[{}\\-]".toRegex(), replacement = "")
    .replace(" ", "")
    .split(".")[0].lowercase()

fun Routing.fetchRes() {
    getOrPost("/get_record") {
        val file = formatFileName( fetchGetOrThrow("file") )
        val format = fetchOrThrow("out_format")
        call.respondText(GetRecord(file, format))
    }

    getOrPost("/get_image") {
        val file = formatFileName( fetchGetOrThrow("file") )
        call.respondText(GetImage(file))
    }

    getOrPost("/upload_group_file") {
        val groupId = fetchOrThrow("group_id")
        val file = fetchOrThrow("file")
        val name = fetchOrThrow("name")
        call.respondText(UploadGroupFile(groupId, file, name))
    }

    getOrPost("/create_group_file_folder") {
        val groupId = fetchOrThrow("group_id")
        val name = fetchOrThrow("name")
        call.respondText(CreateGroupFileFolder(groupId, name))
    }

    getOrPost("/delete_group_folder") {
        val groupId = fetchOrThrow("group_id")
        val id = fetchOrThrow("folder_id")
        call.respondText(DeleteGroupFolder(groupId, id))
    }

    route("/res/[a-fA-F0-9]{32}".toRegex()) {
        get {
            val md5 = call.request.document()
            val file = FileUtils.getFile(md5)
            if (!file.exists()) {
                call.respond(HttpStatusCode.NotFound)
            } else {
                call.respondFile(file)
            }
        }
    }
}