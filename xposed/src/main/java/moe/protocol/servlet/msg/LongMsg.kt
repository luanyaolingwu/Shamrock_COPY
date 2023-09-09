package moe.protocol.servlet.msg

import com.tencent.qqnt.kernel.nativeinterface.MsgConstant
import com.tencent.qqnt.kernel.nativeinterface.MsgRecord
import io.ktor.util.GZip
import moe.fuqiuluo.proto.protobufOf
import moe.fuqiuluo.utils.DeflateTools
import moe.protocol.servlet.BaseSvc

internal object LongMsgHelper: BaseSvc() {

    suspend fun uploadGroupMsg(groupId: String, msgs: List<MsgRecord>) {
        val reqBody = protobufOf(
            2 to mapOf(
                1 to 3,
                2 to 2 to groupId,
                3 to groupId.toLong(),
                4 to DeflateTools.gzip(toGroupByteArray(msgs))
            ),
            15 to mapOf(
                1 to 4,
                2 to 2,
                3 to 9,
                4 to 0
            )
        ).toByteArray()
        sendBufferAW("trpc.group.long_msg_interface.MsgService.SsoSendLongMsg", true, reqBody)
    }

    fun toGroupByteArray(msgs: List<MsgRecord>): ByteArray {
        return protobufOf(
            1 to "MultiMsg",
            2 to msgs.map {  record ->
                mapOf(
                    1 to mapOf(
                        2 to record.senderUid,
                        8 to mapOf(
                            1 to record.peerUin,
                            4 to record.sendNickName,
                            5 to record.chatType
                        )
                    ),
                    2 to mapOf(
                        1 to 82,
                        4 to 0,
                        5 to record.msgSeq,
                        6 to record.msgTime,
                        7 to 1,
                        8 to 0,
                        9 to 0,
                    ),
                    3 to 1 to record.elements.map {
                        mapOf(
                            2 to when (val type = it.elementType) {
                                MsgConstant.KELEMTYPETEXT -> mapOf(1 to 1 to it.textElement.content)

                                else -> error("不支持的合并转发消息类型: $type")
                            }
                        )
                    }
                )
            }
        ).toByteArray()
    }

}