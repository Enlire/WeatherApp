package com.example.weatherapp.data.mappers

import com.example.weatherapp.data.models.HourlyWeatherResponse
import com.example.weatherapp.domain.models.HourlyWeather
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HourlyWeatherMapper {
    fun mapHourlyResponseToDomain(hourlyWeatherResponse: HourlyWeatherResponse): List<HourlyWeather> {
        val hourlyWeatherDataList = mutableListOf<HourlyWeather>()

        for (forecastDayItem in hourlyWeatherResponse.forecast.forecastday) {
            for (hourItem in forecastDayItem.hour) {
                val (formattedDate, formattedTime) = extractDateAndTime(hourItem.time)
                val temperature = hourItem.tempC.toInt()
                val precip = hourItem.precipMm.toInt()
                val dewPoint = hourItem.dewpointC.toInt()
                val windSpeed = hourItem.windKph / 3.6
                val visibility = hourItem.visKm.toInt()
                val uvIndex = hourItem.uv.toInt()
                val pressure = hourItem.pressureIn * 25.4

                val hourlyWeatherData = HourlyWeather(
                    location = hourlyWeatherResponse.location.name,
                    description = hourItem.condition.text,
                    day = formattedDate,
                    hour = formattedTime,
                    icon = hourItem.condition.icon,
                    temperature = temperature,
                    chance_of_rain = hourItem.chanceOfRain,
                    chance_of_snow = hourItem.chanceOfSnow,
                    precip = precip,
                    cloud = hourItem.cloud,
                    dew_point = dewPoint,
                    wind_dir = hourItem.windDir,
                    wind_speed = windSpeed.toInt(),
                    humidity = hourItem.humidity,
                    visibility = visibility,
                    uv_index = uvIndex,
                    pressure = pressure.toInt()
                )
                hourlyWeatherDataList.add(hourlyWeatherData)
            }
        }

        return hourlyWeatherDataList
    }

    fun extractDateAndTime(inputString: String): Pair<String, String> {
        val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val dateTime = LocalDateTime.parse(inputString, dateTimeFormatter)

        // Format the date part as "dd.MM"
        val formattedDate = dateTime.format(DateTimeFormatter.ofPattern("dd.MM"))

        // Format the time part as "HH:mm"
        val formattedTime = dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))

        return Pair(formattedDate, formattedTime)
    }
}