package com.example.weatherapp.data.mappers

import com.example.weatherapp.data.models.CurrentWeatherResponse
import com.example.weatherapp.domain.models.CurrentWeather
import com.example.weatherapp.domain.models.WeatherCondition
import kotlin.math.roundToInt

class CurrentWeatherMapper {
    fun mapCurrentResponseToDomain(response: CurrentWeatherResponse): CurrentWeather {
        val temperature = response.current.tempC.roundToInt()
        val feelsLike = response.current.feelslikeC.roundToInt()
        val windSpeed = response.current.windKph / 3.6
        val visibility = response.current.visKm.roundToInt()
        val uvIndex = response.current.uv.roundToInt()
        val pressure = response.current.pressureIn * 25.4
        val weatherCondition = WeatherCondition()

        return CurrentWeather(
            location = response.location,
            description = response.current.condition.text,
            code = response.current.condition.code,
            isDay = response.current.isDay,
            icResId = weatherCondition.weatherCodeWeatherApiToIcon(response.current.condition.code, response.current.isDay),
            temperature = temperature,
            feelsLike = feelsLike,
            windDir = response.current.windDir,
            windSpeed = windSpeed.roundToInt(),
            humidity = response.current.humidity,
            visibility = visibility,
            uvIndex = uvIndex,
            pressure = pressure.roundToInt()
        )
    }
}