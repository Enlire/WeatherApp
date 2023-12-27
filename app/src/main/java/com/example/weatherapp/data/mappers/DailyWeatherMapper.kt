package com.example.weatherapp.data.mappers

import android.util.Log
import com.example.weatherapp.data.models.DailyWeatherResponse
import com.example.weatherapp.data.models.PastWeatherResponse
import com.example.weatherapp.domain.models.DailyWeather
import com.example.weatherapp.domain.models.PastWeather
import com.example.weatherapp.domain.models.WeatherCondition
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.roundToInt

class DailyWeatherMapper {
    fun mapDailyResponseToDomain (response: DailyWeatherResponse) : List<DailyWeather> {

        val dailyWeatherList = mutableListOf<DailyWeather>()

        val daily = response.daily
        val timeList = daily.time
        val weatherCondition = WeatherCondition()
        val sunriseList = daily.sunrise
        val sunsetList = daily.sunset
        val weatherCode = daily.weathercode
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
            val description = weatherCondition.weatherCodeToDescription(weatherCode[i])
            val iconResId = weatherCondition.weatherCodeOpenMeteoToIcon(weatherCode[i])
            val tempMax = temperature2mMaxList[i].roundToInt()
            val tempMin = temperature2mMinList[i].roundToInt()
            val chanceOfPrecip = precipitationProbabilityMaxList[i]
            val precip = precipitationSumList[i].roundToInt()
            val windSpeed = windspeed10mMaxList[i].roundToInt()
            val uvIndex = uvIndexMaxList[i].roundToInt()
            val sunrise = formatTime(sunriseList[i])
            val sunset = formatTime(sunsetList[i])

            val dailyWeather = DailyWeather(
                dateFormat,
                day,
                description,
                iconResId,
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

    fun mapPastResponseToDomain (response: PastWeatherResponse) : List<PastWeather> {
        val pastWeatherList = mutableListOf<PastWeather>()

        val daily = response.daily
        val timeList = daily.time
        val temperature2mMaxList = daily.temperature2mMax
        val temperature2mMinList = daily.temperature2mMin
        val precipSumList = daily.precipitationSum
        Log.i("presipSum", precipSumList.toString())

        for (i in timeList.indices) {
            val dateFormat = formatDate(timeList[i])
            val tempMax = temperature2mMaxList[i]
            val tempMin = temperature2mMinList[i]
            val precipSum = precipSumList[i]

            val pastWeather = PastWeather(
                dateFormat,
                tempMax,
                tempMin,
                precipSum
            )
            pastWeatherList.add(pastWeather)
        }
        return pastWeatherList
    }

    fun mapCorrelationTempToDomain (response: PastWeatherResponse) : List<Float> {
        val averageTemperatureList = mutableListOf<Float>()
        val precipList = mutableListOf<Float>()
        val daily = response.daily
        val timeList = daily.time
        val temperature2mMaxList = daily.temperature2mMax
        val temperature2mMinList = daily.temperature2mMin
        val precipSumList = daily.precipitationSum

        for (i in timeList.indices) {
            val tempMax = temperature2mMaxList[i]
            val tempMin = temperature2mMinList[i]
            val precipSum = precipSumList[i]

            val averageTemperature = (tempMax + tempMin) / 2

            averageTemperatureList.add(averageTemperature)
            precipList.add(precipSum)
        }

        return averageTemperatureList
    }

    fun mapCorrelationPrecipToDomain (response: PastWeatherResponse) : List<Float> {
        val precipList = mutableListOf<Float>()
        val daily = response.daily
        val timeList = daily.time
        val precipSumList = daily.precipitationSum

        for (i in timeList.indices) {
            val precipSum = precipSumList[i]
            precipList.add(precipSum)
        }

        return precipList
    }

    fun calculateAverageTemperature(
        maxTemperatureList: List<Float>,
        minTemperatureList: List<Float>
    ): List<Float> {
        val result = mutableListOf<Float>()

        val size = maxTemperatureList.size.coerceAtLeast(minTemperatureList.size)

        for (i in 0 until size) {
            val maxTemperature = maxTemperatureList.getOrNull(i) ?: 0.0f
            val minTemperature = minTemperatureList.getOrNull(i) ?: 0.0f

            val averageTemperature = (maxTemperature + minTemperature) / 2.0f
            result.add(averageTemperature)
        }

        return result
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