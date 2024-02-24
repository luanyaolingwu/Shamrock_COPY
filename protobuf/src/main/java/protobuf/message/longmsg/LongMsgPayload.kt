@file:OptIn(ExperimentalSerializationApi::class)

package protobuf.message.longmsg

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import moe.fuqiuluo.symbols.Protobuf
import protobuf.message.PushMsgBody

@Serializable
data class LongMsgContent(
    @ProtoNumber(1) val body: List<PushMsgBody>? = null
)

@Serializable
data class LongMsgAction(
    @ProtoNumber(1) val command: String? = null,
    @ProtoNumber(2) val data: LongMsgContent? = null
)
@Serializable
data class LongMsgPayload(
    @ProtoNumber(2) val action: List<LongMsgAction>? = null
): Protobuf<LongMsgPayload>
