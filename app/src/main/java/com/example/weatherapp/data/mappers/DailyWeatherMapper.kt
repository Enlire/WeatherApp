package com.example.weatherapp.data.mappers

import android.content.Context
import android.content.SharedPreferences
import android.location.Address
import android.location.Geocoder
import androidx.preference.PreferenceManager
import com.example.weatherapp.data.models.DailyWeatherResponse
import com.example.weatherapp.data.models.PastWeatherResponse
import com.example.weatherapp.data.models.WeatherLocation
import com.example.weatherapp.domain.models.DailyWeather
import com.example.weatherapp.domain.models.PastWeather
import com.example.weatherapp.domain.models.WeatherCondition
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter
import java.util.*
import kotlin.math.roundToInt

class DailyWeatherMapper(private val context: Context) {
    private val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

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
            val latitude = response.latitude
            val longitude = response.longitude
            val location = getLocationNameFromCoordinates(latitude, longitude) ?: "unknown"
            val date = timeList[i]
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
                location = location.trim(),
                date = date,
                description = description,
                icResId = iconResId,
                tempMax = tempMax,
                tempMin = tempMin,
                chanceOfPrecip = chanceOfPrecip,
                precip = precip,
                windSpeed = windSpeed,
                uvIndex = uvIndex,
                sunrise = sunrise,
                sunset = sunset
            )
            dailyWeatherList.add(dailyWeather)
        }

        return dailyWeatherList
    }

    fun mapPastResponseToDomain (response: PastWeatherResponse) : List<PastWeather> {
        val pastWeatherList = mutableListOf<PastWeather>()
        val latitude = response.latitude
        val longitude = response.longitude
        val location = getLocationNameFromCoordinates(latitude, longitude) ?: "unknown"
        val daily = response.daily
        val timeList = daily.time
        val temperature2mMaxList = daily.temperature2mMax
        val temperature2mMinList = daily.temperature2mMin
        val precipSumList = daily.precipitationSum

        for (i in timeList.indices) {
            val dateFormat = formatDate(timeList[i])
            val tempMax = temperature2mMaxList[i]
            val tempMin = temperature2mMinList[i]
            val precipSum = precipSumList[i]

            val pastWeather = PastWeather(
                location = location.trim(),
                date = dateFormat,
                tempMax = tempMax,
                tempMin = tempMin,
                presipSum = precipSum
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

    fun mapLocationDailyResponse(
        latitude: Double,
        longitude: Double,
        timezone: String
    ): WeatherLocation {
        val zoneId = ZoneId.of(timezone)
        val name = getLocationNameFromCoordinates(latitude, longitude) ?: "unknown"
        val instant = Instant.now()
        val localDateTime = instant.atZone(zoneId).toLocalDateTime()
        val localTimeEpoch = localDateTime.toEpochSecond(ZoneOffset.UTC)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val localtimeString = localDateTime.format(formatter)

        return WeatherLocation(
            name = name.trim(),
            lon = longitude,
            lat = latitude,
            localtime = localtimeString,
            localTimeEpoch = localTimeEpoch,
            tzId = timezone
        )
    }

    private fun getLocationNameFromCoordinates (latitude: Double, longitude: Double) : String? {
        val geocoder = Geocoder(context, Locale("ru", "RU"))
        try {
            val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses?.isNotEmpty() == true) {
                val address = addresses[0]
                return address.locality
            }
        } catch (e: Exception) {
            preferences.getString("USER_LOCATION", null)
        }
        return preferences.getString("USER_LOCATION", null)
    }

    fun formatDate(dateString: String): String {
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val date = LocalDate.parse(dateString, dateFormatter)

        return date.format(DateTimeFormatter.ofPattern("dd.MM"))
    }

    fun getDayOfWeek(dateString: String): String {
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