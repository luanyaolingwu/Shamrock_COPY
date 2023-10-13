@file:OptIn(DelicateCoroutinesApi::class)

package moe.fuqiuluo.shamrock.remote.service

import com.tencent.qqnt.kernel.nativeinterface.MsgElement
import com.tencent.qqnt.kernel.nativeinterface.MsgRecord
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import moe.fuqiuluo.shamrock.remote.service.api.WebSocketClientServlet
import moe.fuqiuluo.shamrock.remote.service.config.ShamrockConfig
import moe.fuqiuluo.shamrock.remote.service.data.BotStatus
import moe.fuqiuluo.shamrock.remote.service.data.Self
import moe.fuqiuluo.shamrock.remote.service.data.push.*
import moe.fuqiuluo.shamrock.tools.GlobalJson
import moe.fuqiuluo.shamrock.tools.json
import moe.fuqiuluo.qqinterface.servlet.GroupSvc
import moe.fuqiuluo.qqinterface.servlet.msg.toSegment
import mqq.app.MobileQQ
import kotlin.concurrent.timer

internal class WebSocketClientService(
    url: String,
    wsHeaders: Map<String, String>
): WebSocketClientServlet(url, wsHeaders) {
    init {
        timer("heartbeat", true, 0, 1000L * 15) {
            if (isClosed || isClosing || !isOpen) {
                cancel()
                return@timer
            }
            val runtime = MobileQQ.getMobileQQ().waitAppRuntime()
            val curUin = runtime.currentAccountUin
            send(GlobalJson.encodeToString(
                PushMetaEvent(
                time = System.currentTimeMillis() / 1000,
                selfId = app.longAccountUin,
                postType = PostType.Meta,
                type = MetaEventType.Heartbeat,
                subType = MetaSubType.Connect,
                status = BotStatus(
                    Self("qq", curUin.toLong()), runtime.isLogin, status = "正常", good = true
                ),
                interval = 15000
            )
            ))
        }
    }

    override fun pushSelfPrivateSentMsg(
        record: MsgRecord,
        elements: List<MsgElement>,
        raw: String,
        msgHash: Int
    ) {
        pushMsg(
            record,
            elements,
            raw,
            msgHash,
            MsgType.Private,
            MsgSubType.Friend,
            postType = PostType.MsgSent
        )
    }

    override fun pushSelfGroupSentMsg(
        record: MsgRecord,
        elements: List<MsgElement>,
        raw: String,
        msgHash: Int
    ) {
        pushMsg(
            record,
            elements,
            raw,
            msgHash,
            MsgType.Group,
            MsgSubType.NORMAL,
            postType = PostType.MsgSent
        )
    }

    override fun pushPrivateMsg(
        record: MsgRecord,
        elements: List<MsgElement>,
        raw: String,
        msgHash: Int
    ) {
        pushMsg(record, elements, raw, msgHash, MsgType.Private, MsgSubType.Friend)
    }

    override fun pushGroupMsg(
        record: MsgRecord,
        elements: List<MsgElement>,
        raw: String,
        msgHash: Int
    ) {
        pushMsg(
            record, elements, raw, msgHash, MsgType.Group, MsgSubType.NORMAL,
            role = when (record.senderUin) {
                GroupSvc.getOwner(record.peerUin.toString()) -> MemberRole.Owner
                in GroupSvc.getAdminList(record.peerUin.toString()) -> MemberRole.Admin
                else -> MemberRole.Member
            }
        )
    }

    override fun pushGroupPoke(time: Long, operation: Long, userId: Long, groupId: Long) {
        pushNotice(
            time = time,
            type = NoticeType.Notify,
            subType = NoticeSubType.Poke,
            operation = operation,
            userId = operation,
            groupId = groupId,
            target = userId
        )
    }

    override fun pushPrivateMsgRecall(time: Long, operation: Long, msgHash: Int, tip: String) {
        pushNotice(
            time = time,
            type = NoticeType.FriendRecall,
            operation = operation,
            userId = operation,
            msgHash = msgHash,
            tip = tip
        )
    }

    override fun pushGroupMsgRecall(
        time: Long,
        operation: Long,
        userId: Long,
        groupId: Long,
        msgHash: Int,
        tip: String
    ) {
        pushNotice(
            time = time,
            type = NoticeType.GroupRecall,
            operation = operation,
            userId = userId,
            groupId =  groupId,
            msgHash = msgHash,
            tip = tip
        )
    }

    override fun pushGroupBan(
        time: Long,
        operation: Long,
        userId: Long,
        groupId: Long,
        duration: Int
    ) {
        pushNotice(time, NoticeType.GroupBan, if (duration == 0) NoticeSubType.LiftBan else NoticeSubType.Ban, operation, userId, groupId, duration)
    }

    override fun pushGroupMemberDecreased(
        time: Long,
        target: Long,
        groupId: Long,
        operation: Long,
        type: NoticeType,
        subType: NoticeSubType
    ) {
        pushNotice(time, type, subType, operation, target, groupId)
    }

    override fun pushGroupAdminChange(time: Long, target: Long, groupId: Long, setAdmin: Boolean) {
        pushNotice(time, NoticeType.GroupAdminChange, if (setAdmin) NoticeSubType.Set else NoticeSubType.UnSet, 0, target, groupId)
    }

    override fun pushGroupFileCome(
        time: Long,
        userId: Long,
        groupId: Long,
        fileId: String,
        fileName: String,
        fileSize: Long,
        bizId: Int
    ) {
        pushNotice(
            time = time,
            type = NoticeType.GroupUpload,
            groupId = groupId,
            operation = userId,
            userId = userId,
            fileMsg = FileMsg(
                id = fileId,
                name = fileName,
                size = fileSize,
                busid = bizId.toLong()
            )
        )
    }

    private fun pushNotice(
        time: Long,
        type: NoticeType,
        subType: NoticeSubType = NoticeSubType.None,
        operation: Long,
        userId: Long,
        groupId: Long = 0,
        duration: Int = 0,
        msgHash: Int = 0,
        target: Long = 0,
        tip: String = "",
        fileMsg: FileMsg? = null
    ) {
        GlobalScope.launch {
            pushTo(
                PushNotice(
                    time = time,
                    selfId = app.longAccountUin,
                    postType = PostType.Notice,
                    type = type,
                    subType = subType,
                    operatorId = operation,
                    userId = userId,
                    groupId = groupId,
                    duration = duration,
                    target = target,
                    msgId = msgHash,
                    tip = tip,
                    file = fileMsg
                )
            )
        }
    }

    private fun pushMsg(
        record: MsgRecord,
        elements: List<MsgElement>,
        raw: String,
        msgHash: Int,
        msgType: MsgType,
        subType: MsgSubType,
        role: MemberRole = MemberRole.Member,
        postType: PostType = PostType.Msg
    ) {
        GlobalScope.launch {
            pushTo(
                PushMessage(
                    time = record.msgTime,
                    selfId = app.longAccountUin,
                    postType = postType,
                    messageType = msgType,
                    subType = subType,
                    messageId = msgHash,
                    groupId = if(msgType == MsgType.Private) 0 else record.peerUin,
                    peerId = if(msgType != MsgType.Private) 0 else record.peerUin,
                    userId = record.senderUin,
                    message = if (ShamrockConfig.useCQ()) raw.json else elements.toSegment(record.chatType, record.peerUin.toString()).json,
                    rawMessage = raw,
                    font = 0,
                    sender = Sender(
                        userId = record.senderUin,
                        nickname = record.sendNickName,
                        card = record.sendMemberName,
                        role = role,
                        title = "",
                        level = "",
                    )
                )
            )
        }
    }
}