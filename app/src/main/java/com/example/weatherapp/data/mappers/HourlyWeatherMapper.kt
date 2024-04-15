package com.example.weatherapp.data.mappers

import com.example.weatherapp.data.models.HourItem
import com.example.weatherapp.data.models.HourlyWeatherResponse
import com.example.weatherapp.domain.models.HourlyWeather
import com.example.weatherapp.domain.models.WeatherCondition
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

class HourlyWeatherMapper {
    //private val locationService = LocationService(context)
    fun mapHourlyResponseToDomain(response: HourlyWeatherResponse, currentTime: String): List<HourlyWeather> {
        val currentHour = extractCurrentTime(currentTime)//locationService.lastKnownLocation?.time
        val location = response.location.name

        val firstForecastDay = response.forecast.forecastday[0]
        val secondForecastDay = response.forecast.forecastday[1]

        val firstDayHourItems = firstForecastDay.hour.filter { extractTime(it.time) > currentHour && extractTime(it.time) <= "23:00" }
        val secondDayHourItems = secondForecastDay.hour.filter { extractTime(it.time) >= "00:00" && extractTime(it.time) < currentHour }

        val combinedHourItems = mutableListOf<HourlyWeather>()
        combinedHourItems.addAll(mapHourItem(firstDayHourItems, location))
        combinedHourItems.addAll(mapHourItem(secondDayHourItems, location))

        return combinedHourItems
    }

    private fun mapHourItem(response: List<HourItem>, location: String): List<HourlyWeather> {

        val hourlyWeatherList = mutableListOf<HourlyWeather>()

        for (hourItem in response) {
            val (formattedDate, formattedTime) = extractDateAndTime(hourItem.time)
            val temperature = hourItem.tempC.roundToInt()
            val precip = hourItem.precipMm.roundToInt()
            val dewPoint = hourItem.dewpointC.roundToInt()
            val windSpeed = hourItem.windKph / 3.6
            val visibility = hourItem.visKm.roundToInt()
            val uvIndex = hourItem.uv.roundToInt()
            val pressure = hourItem.pressureIn * 25.4
            val weatherCondition = WeatherCondition()

            val hourlyWeather = HourlyWeather(
                description = hourItem.condition.text,
                day = formattedDate,
                hour = formattedTime,
                isDay = hourItem.isDay,
                code = hourItem.condition.code,
                icResId = weatherCondition.weatherCodeWeatherApiToIcon(hourItem.condition.code, hourItem.isDay),
                temperature = temperature,
                chanceOfRain = hourItem.chanceOfRain,
                chanceOfSnow = hourItem.chanceOfSnow,
                precip = precip,
                cloud = hourItem.cloud,
                dewPoint = dewPoint,
                windDir = hourItem.windDir,
                windSpeed = windSpeed.roundToInt(),
                humidity = hourItem.humidity,
                visibility = visibility,
                uvIndex = uvIndex,
                pressure = pressure.roundToInt()
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

    private fun extractCurrentTime(inputString: String): String {
        val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val dateTime = LocalDateTime.parse(inputString, dateTimeFormatter)

        return dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
    }
}