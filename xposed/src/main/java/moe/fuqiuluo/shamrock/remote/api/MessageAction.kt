package moe.fuqiuluo.shamrock.remote.api

import moe.fuqiuluo.shamrock.helper.MessageHelper
import com.tencent.qqnt.kernel.nativeinterface.MsgConstant
import io.ktor.http.ContentType
import io.ktor.server.application.call
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import moe.fuqiuluo.shamrock.helper.db.MessageDB
import moe.fuqiuluo.shamrock.remote.action.handlers.*
import moe.fuqiuluo.shamrock.remote.entries.Status
import moe.fuqiuluo.shamrock.tools.fetchGetOrNull
import moe.fuqiuluo.shamrock.tools.fetchGetOrThrow
import moe.fuqiuluo.shamrock.tools.fetchOrNull
import moe.fuqiuluo.shamrock.tools.fetchOrThrow
import moe.fuqiuluo.shamrock.tools.fetchPostJsonArray
import moe.fuqiuluo.shamrock.tools.fetchPostJsonObject
import moe.fuqiuluo.shamrock.tools.fetchPostJsonString
import moe.fuqiuluo.shamrock.tools.fetchPostOrNull
import moe.fuqiuluo.shamrock.tools.fetchPostOrThrow
import moe.fuqiuluo.shamrock.tools.getOrPost
import moe.fuqiuluo.shamrock.tools.isJsonData
import moe.fuqiuluo.shamrock.tools.isJsonObject
import moe.fuqiuluo.shamrock.tools.isJsonString
import moe.fuqiuluo.shamrock.tools.jsonArray
import moe.fuqiuluo.shamrock.tools.respond

fun Routing.messageAction() {
    route("/send_group_forward_(msg|message)".toRegex()) {
        post {
            val groupId = fetchPostOrNull("group_id")
            val messages = fetchPostJsonArray("messages")
            call.respondText(SendForwardMessage(MsgConstant.KCHATTYPEGROUP, groupId ?: "", messages), ContentType.Application.Json)
        }
        get {
            respond(false, Status.InternalHandlerError, "Not support GET method")
        }
    }

    route("/send_private_forward_(msg|message)".toRegex()) {
        post {
            val userId = fetchPostOrNull("user_id")
            val messages = fetchPostJsonArray("messages")
            call.respondText(SendForwardMessage(MsgConstant.KCHATTYPEC2C, userId ?: "", messages), ContentType.Application.Json)
        }
        get {
            respond(false, Status.InternalHandlerError, "Not support GET method")
        }
    }

    route("/send_forward_(msg|message)".toRegex()) {
        post {
            val userId = fetchPostOrNull("user_id")
            val groupId = fetchPostOrNull("group_id")
            val messages = fetchPostJsonArray("messages")
            call.respondText(SendForwardMessage(MsgConstant.KCHATTYPEC2C, userId ?: groupId?: "", messages), ContentType.Application.Json)
        }
        get {
            respond(false, Status.InternalHandlerError, "Not support GET method")
        }
    }

    getOrPost("/get_forward_msg") {
        val id = fetchOrThrow("id")
        call.respondText(GetForwardMsg(id), ContentType.Application.Json)
    }

    getOrPost("/get_group_msg_history") {
        val peerId = fetchOrThrow("group_id")
        val cnt = fetchOrNull("count")?.toInt() ?: 20
        val startId = fetchOrNull("message_seq")?.toInt()?.let {
            if (it == 0) return@let 0L
            MessageDB.getInstance()
                .messageMappingDao()
                .queryByMsgHashId(it)?.qqMsgId
        } ?: 0L
        call.respondText(GetHistoryMsg("group", peerId, cnt, startId), ContentType.Application.Json)
    }

    getOrPost("/get_history_msg") {
        val msgType = fetchOrThrow("message_type")
        val peerId = fetchOrThrow(if (msgType == "group") "group_id" else "user_id")
        val cnt = fetchOrNull("count")?.toInt() ?: 20
        val startId = fetchOrNull("message_seq")?.toInt()?.let {
            if (it == 0) return@let 0L
            MessageDB.getInstance()
                .messageMappingDao()
                .queryByMsgHashId(it)?.qqMsgId
        } ?: 0L
        call.respondText(GetHistoryMsg(msgType, peerId, cnt, startId), ContentType.Application.Json)
    }

    getOrPost("/clear_msgs") {
        val msgType = fetchOrThrow("message_type")
        val peerId = fetchOrThrow(if (msgType == "group") "group_id" else "user_id")
        call.respondText(ClearMsgs(msgType, peerId), ContentType.Application.Json)
    }

    getOrPost("/delete_msg") {
        val msgHash = fetchOrThrow("message_id").toInt()
        call.respondText(DeleteMessage(msgHash), ContentType.Application.Json)
    }

    getOrPost("/get_msg") {
        val msgHash = fetchOrThrow("message_id").toInt()
        call.respondText(GetMsg(msgHash), ContentType.Application.Json)
    }

    route("/(send_msg|send_message)".toRegex()) {
        get {
            val msgType = fetchGetOrThrow("message_type")
            val message = fetchGetOrThrow("message")
            val retryCnt = fetchGetOrNull("retry_cnt")?.toInt() ?: 3
            val autoEscape = fetchGetOrNull("auto_escape")?.toBooleanStrict() ?: false
            val chatType = MessageHelper.obtainMessageTypeByDetailType(msgType)

            val userId = fetchGetOrNull("user_id")
            val groupId = fetchGetOrNull("group_id")

            val recallDuration = fetchGetOrNull("recall_duration")?.toLongOrNull()

            call.respondText(SendMessage(
                chatType = chatType,
                peerId = if (chatType == MsgConstant.KCHATTYPEC2C) userId!! else groupId!!,
                message = message,
                autoEscape = autoEscape,
                fromId = groupId ?: userId ?: "",
                retryCnt = retryCnt,
                recallDuration = recallDuration
            ), ContentType.Application.Json)
        }
        post {
            val msgType = fetchPostOrThrow("message_type")
            val chatType = MessageHelper.obtainMessageTypeByDetailType(msgType)
            val retryCnt = fetchPostOrNull("retry_cnt")?.toInt() ?: 3

            val userId = fetchPostOrNull("user_id")
            val groupId = fetchPostOrNull("group_id")
            val peerId = if (chatType == MsgConstant.KCHATTYPEC2C) userId!! else groupId!!
            val recallDuration = fetchPostOrNull("recall_duration")?.toLongOrNull()

            call.respondText(if (isJsonData() && !isJsonString("message")) {
                if (isJsonObject("message")) {
                    SendMessage(
                        chatType = chatType,
                        peerId = peerId,
                        message = listOf(fetchPostJsonObject("message")).jsonArray,
                        fromId = groupId ?: userId ?: "",
                        retryCnt = retryCnt,
                        recallDuration = recallDuration
                    )
                } else {
                    SendMessage(
                        chatType = chatType,
                        peerId = peerId,
                        message = fetchPostJsonArray("message"),
                        fromId = groupId ?: userId ?: "",
                        retryCnt = retryCnt,
                        recallDuration = recallDuration
                    )
                }
            } else {
                val autoEscape = fetchPostOrNull("auto_escape")?.toBooleanStrict() ?: false
                //SendMessage(chatType, peerId, fetchPostOrThrow("message"), autoEscape)
                SendMessage(
                    chatType = chatType,
                    peerId = peerId,
                    message = fetchPostOrThrow("message"),
                    autoEscape = autoEscape,
                    fromId = groupId ?: userId ?: "",
                    retryCnt = retryCnt,
                    recallDuration = recallDuration
                )
            }, ContentType.Application.Json)
        }
    }

    route("/send_group_(msg|message)".toRegex()) {
        get {
            val groupId = fetchGetOrThrow("group_id")
            val message = fetchGetOrThrow("message")
            val retryCnt = fetchGetOrNull("retry_cnt")?.toInt() ?: 3
            val autoEscape = fetchGetOrNull("auto_escape")?.toBooleanStrict() ?: false
            val recallDuration = fetchGetOrNull("recall_duration")?.toLongOrNull()

            call.respondText(SendMessage(MsgConstant.KCHATTYPEGROUP, groupId, message, autoEscape, retryCnt = retryCnt, recallDuration = recallDuration), ContentType.Application.Json)
        }
        post {
            val groupId = fetchPostOrThrow("group_id")

            val retryCnt = fetchPostOrNull("retry_cnt")?.toInt() ?: 3
            val autoEscape = fetchPostOrNull("auto_escape")?.toBooleanStrict() ?: false
            val recallDuration = fetchPostOrNull("recall_duration")?.toLongOrNull()

            val result = if (isJsonData()) {
                if (isJsonString("message")) {
                    SendMessage(MsgConstant.KCHATTYPEGROUP, groupId, fetchPostJsonString("message"), autoEscape, retryCnt = retryCnt, recallDuration = recallDuration)
                } else {
                    if (isJsonObject("message")) {
                        SendMessage(
                            chatType = MsgConstant.KCHATTYPEGROUP,
                            peerId = groupId,
                            message = listOf(fetchPostJsonObject("message")).jsonArray,
                            retryCnt = retryCnt,
                            recallDuration = recallDuration
                        )
                    } else {
                        SendMessage(
                            chatType = MsgConstant.KCHATTYPEGROUP,
                            peerId = groupId,
                            message = fetchPostJsonArray("message"),
                            retryCnt = retryCnt,
                            recallDuration = recallDuration
                        )
                    }
                }
            } else {
                SendMessage(MsgConstant.KCHATTYPEGROUP, groupId, fetchPostOrThrow("message"), autoEscape, retryCnt = retryCnt, recallDuration = recallDuration)
            }

            call.respondText(result, ContentType.Application.Json)
        }
    }

    route("/send_private_(msg|message)".toRegex()) {
        get {
            val userId = fetchGetOrThrow("user_id")
            val groupId = fetchGetOrNull("group_id")
            val message = fetchGetOrThrow("message")
            val retryCnt = fetchGetOrNull("retry_cnt")?.toInt() ?: 3
            val autoEscape = fetchGetOrNull("auto_escape")?.toBooleanStrict() ?: false
            val recallDuration = fetchGetOrNull("recall_duration")?.toLongOrNull()
            call.respondText(SendMessage(
                chatType = if (groupId == null) MsgConstant.KCHATTYPEC2C else MsgConstant.KCHATTYPETEMPC2CFROMGROUP,
                peerId = userId,
                message = message,
                autoEscape = autoEscape,
                fromId = groupId ?: userId,
                retryCnt = retryCnt, recallDuration = recallDuration
            ), ContentType.Application.Json)
        }
        post {
            val userId = fetchPostOrThrow("user_id")
            val groupId = fetchPostOrNull("group_id")
            val retryCnt = fetchPostOrNull("retry_cnt")?.toInt() ?: 3
            val autoEscape = fetchPostOrNull("auto_escape")?.toBooleanStrict() ?: false

            val chatType = if (groupId == null) MsgConstant.KCHATTYPEC2C else MsgConstant.KCHATTYPETEMPC2CFROMGROUP
            val fromId = groupId ?: userId
            val recallDuration = fetchPostOrNull("recall_duration")?.toLongOrNull()

            val result = if (isJsonData()) {
                if (isJsonString("message")) {
                    SendMessage(
                        chatType = chatType,
                        peerId = userId,
                        message = fetchPostJsonString("message"),
                        autoEscape = autoEscape,
                        fromId = fromId,
                        retryCnt = retryCnt, recallDuration = recallDuration
                    )
                } else {
                    if (isJsonObject("message")) {
                        SendMessage(
                            chatType = chatType,
                            peerId = userId,
                            message = listOf(fetchPostJsonObject("message")).jsonArray,
                            fromId = fromId,
                            retryCnt = retryCnt, recallDuration = recallDuration
                        )
                    } else {
                        SendMessage(
                            chatType = chatType,
                            peerId = userId,
                            message = fetchPostJsonArray("message"),
                            fromId = fromId,
                            retryCnt = retryCnt, recallDuration = recallDuration
                        )
                    }
                }
            } else {
                SendMessage(
                    chatType = chatType,
                    peerId = userId,
                    message = fetchPostOrThrow("message"),
                    autoEscape = autoEscape,
                    fromId = fromId,
                    retryCnt = retryCnt, recallDuration = recallDuration
                )
            }

            call.respondText(result, ContentType.Application.Json)
        }
    }
}