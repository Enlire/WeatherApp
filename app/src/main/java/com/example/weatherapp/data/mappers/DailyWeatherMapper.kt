package com.example.weatherapp.data.mappers

import com.example.weatherapp.data.models.DailyWeatherResponse
import com.example.weatherapp.domain.models.DailyWeather
import com.example.weatherapp.domain.models.WeatherCondition
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

class DailyWeatherMapper {
    fun mapDailyResponseToDomain (response: DailyWeatherResponse) : List<DailyWeather> {

        val dailyWeatherList = mutableListOf<DailyWeather>()

        val daily = response.daily
        val timeList = daily.time
        val weatherCondition = WeatherCondition()
        val sunriseList = daily.sunrise
        val sunsetList = daily.sunset
        val weatherCodeList = daily.weathercode
        val uvIndexMaxList = daily.uvIndexMax
        val temperature2mMaxList = daily.temperature2mMax
        val temperature2mMinList = daily.temperature2mMin
        val precipitationProbabilityMaxList = daily.precipitationProbabilityMax
        val precipitationSumList = daily.precipitationSum
        val windspeed10mMaxList = daily.windspeed10mMax

        for (i in timeList.indices) {
            val date = timeList[i]
            val dateFormat = formatDate(timeList[i])
            val day = getDayOfWeek(date)
            val description = weatherCondition.weatherCodeToDescription(weatherCodeList[i])
            val tempMax = temperature2mMaxList[i].toInt()
            val tempMin = temperature2mMinList[i].toInt()
            val chanceOfPrecip = precipitationProbabilityMaxList[i]
            val precip = precipitationSumList[i].toInt()
            val windSpeed = windspeed10mMaxList[i].toInt()
            val uvIndex = uvIndexMaxList[i].toInt()
            val sunrise = formatTime(sunriseList[i])
            val sunset = formatTime(sunsetList[i])

            val dailyWeather = DailyWeather(
                dateFormat,
                day,
                description,
                tempMax,
                tempMin,
                chanceOfPrecip,
                precip,
                windSpeed,
                uvIndex,
                sunrise,
                sunset
            )
            dailyWeatherList.add(dailyWeather)
        }

        return dailyWeatherList
    }

    private fun formatDate(dateString: String): String {
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val date = LocalDate.parse(dateString, dateFormatter)

        return date.format(DateTimeFormatter.ofPattern("dd.MM"))
    }

    private fun getDayOfWeek(dateString: String): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
        val date = LocalDate.parse(dateString, formatter)

        val dayOfWeekFormatter = DateTimeFormatter.ofPattern("EE", Locale("ru", "RU"))
        return date.format(dayOfWeekFormatter).uppercase(Locale("ru", "RU"))
    }

    private fun formatTime(timeString: String): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
        val time = LocalTime.parse(timeString, formatter)

        val outputFormatter = DateTimeFormatter.ofPattern("HH:mm")

        return time.format(outputFormatter)
    }
}