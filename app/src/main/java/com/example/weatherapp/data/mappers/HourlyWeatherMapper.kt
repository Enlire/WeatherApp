package com.example.weatherapp.data.mappers

import android.util.Log
import com.example.weatherapp.data.models.HourItem
import com.example.weatherapp.data.models.HourlyWeatherResponse
import com.example.weatherapp.domain.models.HourlyWeather
import com.example.weatherapp.domain.models.WeatherCondition
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class HourlyWeatherMapper {
    fun mapHourlyResponseToDomain(response: HourlyWeatherResponse): List<HourlyWeather> {

        val currentTime = getCurrentTime()
        val location = response.location.name

        val firstForecastday = response.forecast.forecastday[0]
        val secondForecastday = response.forecast.forecastday[1]

        val firstDayHourItems = firstForecastday.hour.filter { extractTime(it.time) > currentTime && extractTime(it.time) <= "23:00" }
        val secondDayHourItems = secondForecastday.hour.filter { extractTime(it.time) >= "00:00" && extractTime(it.time) < currentTime }

        val combinedHourItems = mutableListOf<HourlyWeather>()
        combinedHourItems.addAll(mapHourItem(firstDayHourItems, location))
        combinedHourItems.addAll(mapHourItem(secondDayHourItems, location))

        return combinedHourItems
    }

    private fun mapHourItem(response: List<HourItem>, location: String): List<HourlyWeather> {

        val hourlyWeatherList = mutableListOf<HourlyWeather>()

        for (hourItem in response) {
            val (formattedDate, formattedTime) = extractDateAndTime(hourItem.time)
            val temperature = hourItem.tempC.toInt()
            val precip = hourItem.precipMm.toInt()
            val dewPoint = hourItem.dewpointC.toInt()
            val windSpeed = hourItem.windKph / 3.6
            val visibility = hourItem.visKm.toInt()
            val uvIndex = hourItem.uv.toInt()
            val pressure = hourItem.pressureIn * 25.4
            val weatherCondition = WeatherCondition()

            val hourlyWeather = HourlyWeather(
                location = location,
                description = hourItem.condition.text,
                day = formattedDate,
                hour = formattedTime,
                isDay = hourItem.isDay,
                code = hourItem.condition.code,
                icResId = weatherCondition.weatherCodeWeatherApiToIcon(hourItem.condition.code, hourItem.isDay),
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
            hourlyWeatherList.add(hourlyWeather)
        }

        return hourlyWeatherList
    }

    private fun extractDateAndTime(inputString: String): Pair<String, String> {
        val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val dateTime = LocalDateTime.parse(inputString, dateTimeFormatter)

        // Format the date part as "dd.MM"
        val formattedDate = dateTime.format(DateTimeFormatter.ofPattern("dd.MM"))

        // Format the time part as "HH:mm"
        val formattedTime = dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))

        return Pair(formattedDate, formattedTime)
    }

    private fun extractTime(inputString: String): String {
        val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val dateTime = LocalDateTime.parse(inputString, dateTimeFormatter)

        // Format the time part as "HH:mm"
        return dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
    }

    private fun getCurrentTime(): String {
        val localTime = LocalTime.now()
        return localTime.format(DateTimeFormatter.ofPattern("HH:mm"))
    }
}