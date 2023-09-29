package moe.protocol.service.data.push

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal enum class MetaEventType {
    @SerialName("lifecycle") LifeCycle,
    @SerialName("heartbeat") Heartbeat
}

@Serializable
internal enum class MetaSubType {
    @SerialName("enable") Enable,
    @SerialName("disable") Disable,
    @SerialName("connect") Connect,
}

/**
 * 不要使用继承的方式实现通用字段，那样会很难维护！
 */
@Serializable
internal data class PushMetaEvent(
    @SerialName("time") val time: Long,
    @SerialName("self_id") val selfId: Long,
    @SerialName("post_type") val postType: PostType,
    @SerialName("meta_event_type") val type: MetaEventType,
    @SerialName("sub_type") val subType: MetaSubType,
)