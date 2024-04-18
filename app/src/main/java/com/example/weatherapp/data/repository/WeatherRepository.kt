package com.example.weatherapp.data.repository

import androidx.lifecycle.LiveData
import com.example.weatherapp.domain.models.CurrentWeather
import com.example.weatherapp.data.models.WeatherLocation
import com.example.weatherapp.domain.models.DailyWeather
import com.example.weatherapp.domain.models.HourlyWeather
import com.example.weatherapp.domain.models.PastWeather
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime

interface WeatherRepository {
    suspend fun getCurrentWeatherDataFromDb(): LiveData<out CurrentWeather>
    suspend fun getHourlyWeatherDataFromDb(startHour: LocalDateTime): LiveData<out List<HourlyWeather>>
    suspend fun getDailyWeatherDataFromDb(startDate: LocalDate): LiveData<out List<DailyWeather>>
    suspend fun getPastWeatherFromDb(): LiveData<out List<PastWeather>>
    suspend fun getWeatherLocationFromDb(): LiveData<WeatherLocation>
}