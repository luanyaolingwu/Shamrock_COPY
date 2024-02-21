package moe.fuqiuluo.qqinterface.servlet.msg.messageelement

import kotlinx.io.core.ByteReadPacket
import kotlinx.io.core.discardExact
import kotlinx.io.core.readUInt
import moe.fuqiuluo.qqinterface.servlet.msg.MessageSegment
import moe.fuqiuluo.shamrock.helper.Level
import moe.fuqiuluo.shamrock.helper.LogCenter
import protobuf.message.MessageElement


internal suspend fun List<MessageElement>.toSegments(
    chatType: Int,
    peerId: String,
    subPeer: String
): List<MessageSegment> {
    val messageData = arrayListOf<MessageSegment>()
    this.forEach { msg ->
        kotlin.runCatching {
            val elementType = if (msg.text != null) {
                1
            } else if (msg.face != null) {
                2
            } else if (msg.json != null) {
                51
            } else
                throw UnsupportedOperationException("不支持的消息element类型：$msg")
            val converter = MessageElementConverter[elementType]
            converter?.invoke(chatType, peerId, subPeer, msg)
                ?: throw UnsupportedOperationException("不支持的消息element类型：$elementType")
        }.onSuccess {
            messageData.add(it)
        }.onFailure {
            if (it is UnknownError) {
                // 不处理的消息类型，抛出unknown error
            } else {
                LogCenter.log("消息element转换错误：$it", Level.WARN)
            }
        }
    }
    return messageData
}

internal typealias IMessageElementConverter = suspend (Int, String, String, MessageElement) -> MessageSegment

internal object MessageElementConverter {
    private val convertMap = hashMapOf(
          1 to MessageElementConverter::convertTextElem,
//        MsgConstant.KELEMTYPEFACE to MessageElementConverter::convertFaceElem,
//        MsgConstant.KELEMTYPEPIC to MessageElementConverter::convertImageElem,
//        MsgConstant.KELEMTYPEPTT to MessageElementConverter::convertVoiceElem,
//        MsgConstant.KELEMTYPEVIDEO to MessageElementConverter::convertVideoElem,
//        MsgConstant.KELEMTYPEMARKETFACE to MessageElementConverter::convertMarketFaceElem,
//        MsgConstant.KELEMTYPEARKSTRUCT to MessageElementConverter::convertStructJsonElem,
//        MsgConstant.KELEMTYPEREPLY to MessageElementConverter::convertReplyElem,
//        MsgConstant.KELEMTYPEGRAYTIP to MessageElementConverter::convertGrayTipsElem,
//        MsgConstant.KELEMTYPEFILE to MessageElementConverter::convertFileElem,
//        MsgConstant.KELEMTYPEMARKDOWN to MessageElementConverter::convertMarkdownElem,
//        //MsgConstant.KELEMTYPEMULTIFORWARD to MessageElementConverter::convertXmlMultiMsgElem,
//        //MsgConstant.KELEMTYPESTRUCTLONGMSG to MessageElementConverter::convertXmlLongMsgElem,
//        MsgConstant.KELEMTYPEFACEBUBBLE to MessageElementConverter::convertBubbleFaceElem,
//        MsgConstant.KELEMTYPEINLINEKEYBOARD to MessageElementConverter::convertInlineKeyboardElem,
    )

    operator fun get(type: Int): IMessageElementConverter? = convertMap[type]

    /**
     * 文本 / 艾特 消息转换消息段
     */
    private suspend fun convertTextElem(
        chatType: Int,
        peerId: String,
        subPeer: String,
        element: MessageElement
    ): MessageSegment {
        val text = element.text!!
        if (text.attr6Buf != null) {
            val at = ByteReadPacket(text.attr6Buf!!)
            at.discardExact(7)
            val uin = at.readUInt()
            return MessageSegment(
                type = "at",
                data = hashMapOf(
                    "qq" to uin
                )
            )
        } else if (text.pbReserve != null) {
            val resv = text.pbReserve!!
            return MessageSegment(
                type = "at",
                data = hashMapOf(
                    "qq" to when (resv.atType) {
                        2 -> resv.atMemberTinyid!!
                        4 -> resv.atChannelInfo!!.channelId!!
                        else -> throw UnsupportedOperationException("Unknown at type: ${resv.atType}")
                    }
                )
            )
        } else {
            return MessageSegment(
                type = "text",
                data = hashMapOf(
                    "text" to text.text!!
                )
            )
        }
    }

//    /**
//     * 小表情 / 戳一戳 消息转换消息段
//     */
//    private suspend fun convertFaceElem(
//        chatType: Int,
//        peerId: String,
//        subPeer: String,
//        element: MessageElement
//    ): MessageSegment {
//        val face = element.faceElement
//
//        if (face.faceType == 5) {
//            return MessageSegment(
//                type = "poke",
//                data = hashMapOf(
//                    "type" to face.pokeType,
//                    "id" to face.vaspokeId,
//                    "strength" to face.pokeStrength
//                )
//            )
//        }
//        when (face.faceIndex) {
//            114 -> {
//                return MessageSegment(
//                    type = "basketball",
//                    data = hashMapOf(
//                        "id" to face.resultId.ifEmpty { "0" }.toInt(),
//                    )
//                )
//            }
//
//            358 -> {
//                if (face.sourceType == 1) return MessageSegment("new_dice")
//                return MessageSegment(
//                    type = "new_dice",
//                    data = hashMapOf(
//                        "id" to face.resultId.ifEmpty { "0" }.toInt()
//                    )
//                )
//            }
//
//            359 -> {
//                if (face.resultId.isEmpty()) return MessageSegment("new_rps")
//                return MessageSegment(
//                    type = "new_rps",
//                    data = hashMapOf(
//                        "id" to face.resultId.ifEmpty { "0" }.toInt()
//                    )
//                )
//            }
//
//            394 -> {
//                //LogCenter.log(face.toString())
//                return MessageSegment(
//                    type = "face",
//                    data = hashMapOf(
//                        "id" to face.faceIndex,
//                        "big" to (face.faceType == 3),
//                        "result" to (face.resultId ?: "1")
//                    )
//                )
//            }
//
//            else -> return MessageSegment(
//                type = "face",
//                data = hashMapOf(
//                    "id" to face.faceIndex,
//                    "big" to (face.faceType == 3)
//                )
//            )
//        }
//    }
//
//    /**
//     * 图片消息转换消息段
//     */
//    private suspend fun convertImageElem(
//        chatType: Int,
//        peerId: String,
//        subPeer: String,
//        element: MessageElement
//    ): MessageSegment {
//        val image = element.picElement
//        val md5 = image.md5HexStr ?: image.fileName
//            .replace("{", "")
//            .replace("}", "")
//            .replace("-", "").split(".")[0]
//
//        ImageDB.getInstance().imageMappingDao().insert(
//            ImageMapping(md5.uppercase(), chatType, image.fileSize)
//        )
//
//        //LogCenter.log(image.toString())
//
//        val originalUrl = image.originImageUrl ?: ""
//        //LogCenter.log({ "receive image: $image" }, Level.DEBUG)
//
//        return MessageSegment(
//            type = "image",
//            data = hashMapOf(
//                "file" to md5,
//                "url" to when (chatType) {
//                    MsgConstant.KCHATTYPEDISC, MsgConstant.KCHATTYPEGROUP -> RichProtoSvc.getGroupPicDownUrl(
//                        originalUrl,
//                        md5
//                    )
//
//                    MsgConstant.KCHATTYPEC2C -> RichProtoSvc.getC2CPicDownUrl(originalUrl, md5)
//                    MsgConstant.KCHATTYPEGUILD -> RichProtoSvc.getGuildPicDownUrl(originalUrl, md5)
//                    else -> throw UnsupportedOperationException("Not supported chat type: $chatType")
//                },
//                "subType" to image.picSubType,
//                "type" to if (image.isFlashPic == true) "flash" else if (image.original) "original" else "show"
//            )
//        )
//    }
//
//    /**
//     * 语音消息转换消息段
//     */
//    private suspend fun convertVoiceElem(
//        chatType: Int,
//        peerId: String,
//        subPeer: String,
//        element: MessageElement
//    ): MessageSegment {
//        val record = element.pttElement
//
//        val md5 = if (record.fileName.startsWith("silk"))
//            record.fileName.substring(5)
//        else record.md5HexStr
//
//        return MessageSegment(
//            type = "record",
//            data = hashMapOf(
//                "file" to md5,
//                "url" to when (chatType) {
//                    MsgConstant.KCHATTYPEGROUP -> RichProtoSvc.getGroupPttDownUrl(
//                        "0",
//                        record.md5HexStr,
//                        record.fileUuid
//                    )
//
//                    MsgConstant.KCHATTYPEC2C -> RichProtoSvc.getC2CPttDownUrl("0", record.fileUuid)
//                    MsgConstant.KCHATTYPEGUILD -> RichProtoSvc.getGroupPttDownUrl(
//                        "0",
//                        record.md5HexStr,
//                        record.fileUuid
//                    )
//
//                    else -> throw UnsupportedOperationException("Not supported chat type: $chatType")
//                }
//            ).also {
//                if (record.voiceChangeType != MsgConstant.KPTTVOICECHANGETYPENONE) {
//                    it["magic"] = "1"
//                }
//                if ((it["url"] as String).isBlank()) {
//                    it.remove("url")
//                }
//            }
//        )
//    }
//
//    /**
//     * 视频消息转换消息段
//     */
//    private suspend fun convertVideoElem(
//        chatType: Int,
//        peerId: String,
//        subPeer: String,
//        element: MessageElement
//    ): MessageSegment {
//        val video = element.videoElement
//        val md5 = if (video.fileName.contains("/")) {
//            video.videoMd5.takeIf {
//                !it.isNullOrEmpty()
//            }?.hex2ByteArray() ?: video.fileName.split("/").let {
//                it[it.size - 2].hex2ByteArray()
//            }
//        } else video.fileName.split(".")[0].hex2ByteArray()
//
//        //LogCenter.log({ "receive video msg: $video" }, Level.DEBUG)
//
//        return MessageSegment(
//            type = "video",
//            data = hashMapOf(
//                "file" to video.fileName,
//                "url" to when (chatType) {
//                    MsgConstant.KCHATTYPEGROUP -> RichProtoSvc.getGroupVideoDownUrl("0", md5, video.fileUuid)
//                    MsgConstant.KCHATTYPEC2C -> RichProtoSvc.getC2CVideoDownUrl("0", md5, video.fileUuid)
//                    MsgConstant.KCHATTYPEGUILD -> RichProtoSvc.getGroupVideoDownUrl("0", md5, video.fileUuid)
//                    else -> throw UnsupportedOperationException("Not supported chat type: $chatType")
//                }
//            ).also {
//                if ((it["url"] as String).isBlank())
//                    it.remove("url")
//            }
//        )
//    }
//
//    /**
//     * 商城大表情消息转换消息段
//     */
//    private suspend fun convertMarketFaceElem(
//        chatType: Int,
//        peerId: String,
//        subPeer: String,
//        element: MessageElement
//    ): MessageSegment {
//        val face = element.marketFaceElement
//        return when (face.emojiId.lowercase()) {
//            "4823d3adb15df08014ce5d6796b76ee1" -> MessageSegment("dice")
//            "83c8a293ae65ca140f348120a77448ee" -> MessageSegment("rps")
//            else -> MessageSegment(
//                type = "mface",
//                data = hashMapOf(
//                    "id" to face.emojiId
//                )
//            )
//        }
//    }
//
//    /**
//     * JSON消息转消息段
//     */
//    private suspend fun convertStructJsonElem(
//        chatType: Int,
//        peerId: String,
//        subPeer: String,
//        element: MessageElement
//    ): MessageSegment {
//        val data = element.arkElement.bytesData.asJsonObject
//        return when (data["app"].asString) {
//            "com.tencent.multimsg" -> {
//                val info = data["meta"].asJsonObject["detail"].asJsonObject
//                MessageSegment(
//                    type = "forward",
//                    data = mapOf(
//                        "id" to info["resid"].asString
//                    )
//                )
//            }
//
//            "com.tencent.troopsharecard" -> {
//                val info = data["meta"].asJsonObject["contact"].asJsonObject
//                MessageSegment(
//                    type = "contact",
//                    data = hashMapOf(
//                        "type" to "group",
//                        "id" to info["jumpUrl"].asString.split("group_code=")[1]
//                    )
//                )
//            }
//
//            "com.tencent.contact.lua" -> {
//                val info = data["meta"].asJsonObject["contact"].asJsonObject
//                MessageSegment(
//                    type = "contact",
//                    data = hashMapOf(
//                        "type" to "private",
//                        "id" to info["jumpUrl"].asString.split("uin=")[1]
//                    )
//                )
//            }
//
//            "com.tencent.map" -> {
//                val info = data["meta"].asJsonObject["Location.Search"].asJsonObject
//                MessageSegment(
//                    type = "location",
//                    data = hashMapOf(
//                        "lat" to info["lat"].asString,
//                        "lon" to info["lng"].asString,
//                        "content" to info["address"].asString,
//                        "title" to info["name"].asString
//                    )
//                )
//            }
//
//            else -> MessageSegment(
//                type = "json",
//                data = mapOf(
//                    "data" to element.arkElement.bytesData.asJsonObject.toString()
//                )
//            )
//        }
//    }
//
//    /**
//     * 回复消息转消息段
//     */
//    private suspend fun convertReplyElem(
//        chatType: Int,
//        peerId: String,
//        subPeer: String,
//        element: MessageElement
//    ): MessageSegment {
//        val reply = element.replyElement
//        val msgId = reply.replayMsgId
//        val msgHash = if (msgId != 0L) {
//            MessageHelper.generateMsgIdHash(chatType, msgId)
//        } else {
//            MessageDB.getInstance().messageMappingDao()
//                .queryByMsgSeq(chatType, peerId, reply.replayMsgSeq?.toInt() ?: 0)?.msgHashId
//                ?: kotlin.run {
//                    LogCenter.log("消息映射关系未找到: Message($reply)", Level.WARN)
//                    MessageHelper.generateMsgIdHash(chatType, reply.sourceMsgIdInRecords)
//                }
//        }
//
//        return MessageSegment(
//            type = "reply",
//            data = mapOf(
//                "id" to msgHash
//            )
//        )
//    }
//
//    /**
//     * 灰色提示条消息过滤
//     */
//    private suspend fun convertGrayTipsElem(
//        chatType: Int,
//        peerId: String,
//        subPeer: String,
//        element: MessageElement
//    ): MessageSegment {
//        val tip = element.grayTipElement
//        when (tip.subElementType) {
//            MsgConstant.GRAYTIPELEMENTSUBTYPEJSON -> {
//                val notify = tip.jsonGrayTipElement
//                when (notify.busiId) {
//                    /* 新人入群 */ 17L, /* 群戳一戳 */1061L,
//                    /* 群撤回 */1014L, /* 群设精消息 */2401L,
//                    /* 群头衔 */2407L -> {
//                }
//
//                    else -> LogCenter.log("不支持的灰条类型(JSON): ${notify.busiId}", Level.WARN)
//                }
//            }
//
//            MsgConstant.GRAYTIPELEMENTSUBTYPEXMLMSG -> {
//                val notify = tip.xmlElement
//                when (notify.busiId) {
//                    /* 群戳一戳 */1061L, /* 群打卡 */1068L -> {}
//                    else -> LogCenter.log("不支持的灰条类型(XML): ${notify.busiId}", Level.WARN)
//                }
//            }
//
//            else -> LogCenter.log("不支持的提示类型: ${tip.subElementType}", Level.WARN)
//        }
//        // 提示类消息，这里提供的是一个xml，不具备解析通用性
//        // 在这里不推送
//        throw UnknownError()
//    }
//
//    /**
//     * 文件消息转换消息段
//     */
//    private suspend fun convertFileElem(
//        chatType: Int,
//        peerId: String,
//        subPeer: String,
//        element: MessageElement
//    ): MessageSegment {
//        val fileMsg = element.fileElement
//        val fileName = fileMsg.fileName
//        val fileSize = fileMsg.fileSize
//        val expireTime = fileMsg.expireTime ?: 0
//        val fileId = fileMsg.fileUuid
//        val bizId = fileMsg.fileBizId ?: 0
//        val fileSubId = fileMsg.fileSubId ?: ""
//        val url = when (chatType) {
//            MsgConstant.KCHATTYPEC2C -> RichProtoSvc.getC2CFileDownUrl(fileId, fileSubId)
//            MsgConstant.KCHATTYPEGUILD -> RichProtoSvc.getGuildFileDownUrl(peerId, subPeer, fileId, bizId)
//            else -> RichProtoSvc.getGroupFileDownUrl(peerId.toLong(), fileId, bizId)
//        }
//
//        return MessageSegment(
//            type = "file",
//            data = mapOf(
//                "name" to fileName,
//                "size" to fileSize,
//                "expire" to expireTime,
//                "id" to fileId,
//                "url" to url,
//                "biz" to bizId,
//                "sub" to fileSubId
//            )
//        )
//    }
//
//    /**
//     * 老板QQ的合并转发信息
//     */
//    private suspend fun convertXmlMultiMsgElem(
//        chatType: Int,
//        peerId: String,
//        subPeer: String,
//        element: MessageElement
//    ): MessageSegment {
//        val multiMsg = element.multiForwardMessageElement
//        return MessageSegment(
//            type = "forward",
//            data = mapOf(
//                "id" to multiMsg.resId
//            )
//        )
//    }
//
//    private suspend fun convertXmlLongMsgElem(
//        chatType: Int,
//        peerId: String,
//        subPeer: String,
//        element: MessageElement
//    ): MessageSegment {
//        val longMsg = element.structLongMessageElement
//        return MessageSegment(
//            type = "forward",
//            data = mapOf(
//                "id" to longMsg.resId
//            )
//        )
//    }
//
//    private suspend fun convertMarkdownElem(
//        chatType: Int,
//        peerId: String,
//        subPeer: String,
//        element: MessageElement
//    ): MessageSegment {
//        val markdown = element.markdownElement
//        return MessageSegment(
//            type = "markdown",
//            data = mapOf(
//                "content" to markdown.content
//            )
//        )
//    }
//
//    private suspend fun convertBubbleFaceElem(
//        chatType: Int,
//        peerId: String,
//        subPeer: String,
//        element: MessageElement
//    ): MessageSegment {
//        val bubbleElement = element.faceBubbleElement
//        return MessageSegment(
//            type = "bubble_face",
//            data = mapOf(
//                "id" to bubbleElement.yellowFaceInfo.index,
//                "count" to (bubbleElement.faceCount ?: 1),
//            )
//        )
//    }
//
//    private suspend fun convertInlineKeyboardElem(
//        chatType: Int,
//        peerId: String,
//        subPeer: String,
//        element: MessageElement
//    ): MessageSegment {
//        val keyboard = element.inlineKeyboardElement
//        return MessageSegment(
//            type = "inline_keyboard",
//            data = mapOf(
//                "data" to buildJsonObject {
//                    putJsonArray("rows") {
//                        keyboard.rows.forEach { row ->
//                            add(buildJsonObject row@{
//                                putJsonArray("buttons") {
//                                    row.buttons.forEach { button ->
//                                        add(buildJsonObject {
//                                            put("id", button.id ?: "")
//                                            put("label", button.label ?: "")
//                                            put("visited_label", button.visitedLabel ?: "")
//                                            put("style", button.style)
//                                            put("type", button.type)
//                                            put("click_limit", button.clickLimit)
//                                            put("unsupport_tips", button.unsupportTips ?: "")
//                                            put("data", button.data)
//                                            put("at_bot_show_channel_list", button.atBotShowChannelList)
//                                            put("permission_type", button.permissionType)
//                                            putJsonArray("specify_role_ids") {
//                                                button.specifyRoleIds?.forEach { add(it) }
//                                            }
//                                            putJsonArray("specify_tinyids") {
//                                                button.specifyTinyids?.forEach { add(it) }
//                                            }
//                                        })
//                                    }
//                                }
//                            })
//                        }
//                    }
//                    put("bot_appid", keyboard.botAppid)
//                }.toString()
//            )
//        )
//    }
}
