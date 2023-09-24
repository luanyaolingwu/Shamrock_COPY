package moe.fuqiuluo.remote.action.handlers

import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsText
import io.ktor.http.encodeURLQueryComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import moe.fuqiuluo.remote.action.ActionSession
import moe.fuqiuluo.remote.action.IActionHandler
import moe.fuqiuluo.xposed.tools.GlobalClient
import moe.fuqiuluo.xposed.tools.GlobalJson
import moe.fuqiuluo.xposed.tools.json
import moe.protocol.servlet.TicketSvc

internal object GetModelShowList : IActionHandler() {
    override suspend fun internalHandle(session: ActionSession): String {
        return invoke(session.getString("model"), session.echo)
    }

    suspend operator fun invoke(model: String, echo: String = ""): String {

        val ts = System.nanoTime() / 1e6
        val csrf = TicketSvc.getCSRF(TicketSvc.getUin(), "vip.qq.com")

        val req = mapOf(
            "13030" to mapOf(
                "req" to mapOf(
                    "lUin" to TicketSvc.getUin().toLong(),
                    "sModel" to model.encodeURLQueryComponent(),
                    "iAppType" to 0,
                    "sIMei" to "",
                    "bShowInfo" to true,
                    "sModelShow" to "",
                    "bRecoverDefault" to false
                )
            )
        ).json.toString()

        val resp = GlobalClient.get {
            url("https://proxy.vip.qq.com/cgi-bin/srfentry.fcgi?ts=$ts&daid=18&g_tk=$csrf&pt4_token=&data=$req")
            val cookie = TicketSvc.getCookie("vip.qq.com")
            header("Cookie", cookie)
        }.bodyAsText()

        val json = GlobalJson.decodeFromString<ModelShowStruct>(resp)

        if (json.resp == null) {
            return error("unable to fetch model show list", echo)
        }

        return ok(GetModelListResp(json.resp.data.rsp.vItemList.map {
            Model(it.sModelShow, it.bNeedPay)
        }), echo)
    }

    override val requiredParams: Array<String> = arrayOf("model")

    override fun path(): String = "_get_model_show"


    @Serializable
    data class GetModelListResp(
        @SerialName("variants") val resp: List<Model>
    )

    @Serializable
    data class Model(
        @SerialName("model_show") val model: String,
        @SerialName("need_pay") val needPay: Boolean
    )

    @Serializable
    data class ModelShowStruct(
        @SerialName("13030") val resp: ModelGetResp? = null
    )

    @Serializable
    data class ModelGetResp(
        @SerialName("data") val data: ModelGetData
    )

    @Serializable
    data class ModelGetData(
        @SerialName("rsp") val rsp: ModelGetRsp
    )

    @Serializable
    data class ModelGetRsp(
        @SerialName("vItemList") val vItemList: List<ModelGetItem>
    )

    @Serializable
    data class ModelGetItem(
        @SerialName("sModelShow") val sModelShow: String,
        @SerialName("bNeedPay") val bNeedPay: Boolean
    )
}