package moe.fuqiuluo.shamrock.remote.action.handlers

import moe.fuqiuluo.qqinterface.servlet.ark.WeatherSvc
import moe.fuqiuluo.shamrock.remote.action.ActionSession
import moe.fuqiuluo.shamrock.remote.action.IActionHandler

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