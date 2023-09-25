package moe.protocol.servlet.ark

import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.http.encodeURLQueryComponent
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import moe.fuqiuluo.xposed.helper.Level
import moe.fuqiuluo.xposed.helper.LogCenter
import moe.fuqiuluo.xposed.tools.GlobalClient
import moe.fuqiuluo.xposed.tools.GlobalJson
import moe.fuqiuluo.xposed.tools.asInt
import moe.fuqiuluo.xposed.tools.asJsonArray
import moe.fuqiuluo.xposed.tools.asJsonObject
import moe.fuqiuluo.xposed.tools.asString
import moe.fuqiuluo.xposed.tools.asStringOrNull
import moe.protocol.servlet.TicketSvc
import java.lang.Exception

@Serializable
data class Region(
    val adcode: Int,
    val province: String?,
    val city: String?
)

internal object WeatherSvc {
    suspend fun fetchWeatherCard(code: Int): Result<JsonObject> {
        val cookie = TicketSvc.getCookie("mp.qq.com")
        val resp = GlobalClient.get("https://weather.mp.qq.com/page/poster?_wv=2&&_wwv=4&adcode=$code") {
            header("Cookie", cookie)
        }

        if (resp.status != HttpStatusCode.OK) {
            LogCenter.log("fetchWeatherCard: error: ${resp.status}, cookie: $cookie", Level.ERROR)
            return Result.failure(Exception("search city failed"))
        }

        val textJson = resp.bodyAsText()
            .replace("\n", "")
            .split("window.__INITIAL_STATE__ =")[1]
                .split("};")[0].trim() + "}"

        //LogCenter.log(textJson)

        return Result.success(Json.parseToJsonElement(textJson).asJsonObject)
    }

    suspend fun searchCity(query: String): Result<List<Region>> {
        val pskey = TicketSvc.getPSKey(TicketSvc.getUin(), "mp.qq.com") ?: ""
        val cookie = TicketSvc.getCookie("mp.qq.com")
        val gtk = TicketSvc.getCSRF(pskey)
        val resp = GlobalClient.get {
            url("https://weather.mp.qq.com/trpc/weather/SearchRegions?g_tk=$gtk&key=${query.encodeURLQueryComponent()}&offset=0&count=25")
            header("Cookie", cookie)
        }

        if (resp.status != HttpStatusCode.OK) {
            LogCenter.log("GetWeatherCityCode: error: ${resp.status}, cookie: $cookie, bkn: $gtk", Level.ERROR)
            return Result.failure(Exception("search city failed"))
        }

        val json = GlobalJson.parseToJsonElement(resp.bodyAsText()).asJsonObject


        val cnt = json["totalCount"].asInt
        if (cnt == 0) {
            return Result.success(emptyList())
        }

        val regions = json["regions"].asJsonArray.map {
            val region = it.asJsonObject
            Region(
                region["adcode"].asInt,
                region["province"].asStringOrNull,
                region["city"].asStringOrNull
            )
        }

        return Result.success(regions)
    }

}