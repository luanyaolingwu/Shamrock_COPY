package moe.fuqiuluo.remote.action.handlers

import moe.fuqiuluo.remote.action.ActionSession
import moe.fuqiuluo.remote.action.IActionHandler
import moe.fuqiuluo.xposed.helper.Level
import moe.fuqiuluo.xposed.helper.LogCenter
import moe.protocol.servlet.ark.WeatherSvc

internal object GetWeatherCityCode: IActionHandler() {
    override suspend fun internalHandle(session: ActionSession): String {
        val city = session.getStringOrNull("city")
            ?: return invoke(session.echo)
        return invoke(city, session.echo)
    }

    suspend operator fun invoke(city: String, echo: String = ""): String {
        val result = WeatherSvc.searchCity(city)
        if (result.isFailure) {
            return error(result.exceptionOrNull()?.message ?: "unknown error", echo)
        }

        val regions = result.getOrThrow()

        return ok(regions, echo)
    }

    override fun path(): String = "get_weather_city_code"


    override val requiredParams: Array<String> = arrayOf("city")
}