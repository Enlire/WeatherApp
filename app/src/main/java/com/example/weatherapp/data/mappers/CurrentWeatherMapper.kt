package com.example.weatherapp.data.mappers

import com.example.weatherapp.data.models.CurrentWeatherResponse
import com.example.weatherapp.domain.models.CurrentWeather
import com.example.weatherapp.domain.models.WeatherCondition

class CurrentWeatherMapper {
    fun mapCurrentResponseToDomain(response: CurrentWeatherResponse): CurrentWeather {
        val temperature = response.current.tempC.toInt()
        val feelsLike = response.current.feelslikeC.toInt()
        val windSpeed = response.current.windKph / 3.6
        val visibility = response.current.visKm.toInt()
        val uvIndex = response.current.uv.toInt()
        val pressure = response.current.pressureIn * 25.4
        val weatherCondition = WeatherCondition()

        return CurrentWeather(
            location = response.location.name,
            description = response.current.condition.text,
            code = response.current.condition.code,
            isDay = response.current.isDay,
            icResId = weatherCondition.weatherCodeWeatherApiToIcon(response.current.condition.code, response.current.isDay),
            temperature = temperature,
            feels_like = feelsLike,
            wind_dir = response.current.windDir,
            wind_speed = windSpeed.toInt(),
            humidity = response.current.humidity,
            visibility = visibility,
            uv_index = uvIndex,
            pressure = pressure.toInt()
        )
    }
}