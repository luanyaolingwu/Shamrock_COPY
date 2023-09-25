package moe.fuqiuluo.remote.api

import io.ktor.server.application.call
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import moe.fuqiuluo.remote.action.handlers.GetWeatherCityCode
import moe.fuqiuluo.xposed.tools.fetchOrThrow
import moe.fuqiuluo.xposed.tools.getOrPost

fun Routing.weatherAction() {
    getOrPost("/get_weather_city_code") {
        val city = fetchOrThrow("city")
        call.respondText(GetWeatherCityCode(city))
    }
}