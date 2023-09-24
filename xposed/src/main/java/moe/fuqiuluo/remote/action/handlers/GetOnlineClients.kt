package moe.fuqiuluo.remote.action.handlers

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import moe.fuqiuluo.remote.action.ActionSession
import moe.fuqiuluo.remote.action.IActionHandler
import moe.fuqiuluo.xposed.tools.toHexString
import moe.protocol.servlet.QSafeSvc

internal object GetOnlineClients: IActionHandler() {
    override suspend fun internalHandle(session: ActionSession): String {
        return invoke(session.echo)
    }

    suspend operator fun invoke(echo: String = ""): String {
        val clients = QSafeSvc.getOnlineClients()
            ?: return logic("获取在线设备信息失败", echo)
        return ok(clients.map {
            DevInfo(it.iAppId, it.strDeviceName, it.strDeviceTypeInfo, it.iLoginTime,
                it.iLoginPlatform, it.strLoginLocation,
                // it.vecGuid.toHexString() ignore this field
                )
        }, echo)
    }

    override fun path(): String = "_get_online_clients"

    @Serializable
    data class DevInfo(
        @SerialName("app_id") val appId: Long,
        @SerialName("device_name") val deviceName: String?,
        @SerialName("device_kind") val deviceType: String?,
        @SerialName("login_time") val loginTime: Long?,
        @SerialName("login_platform") val loginPlatform: Long?,
        @SerialName("location") val location: String?,
        @SerialName("guid") val guid: String? = ""
    )
}