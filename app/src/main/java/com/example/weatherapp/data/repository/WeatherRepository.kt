package com.example.weatherapp.data.repository

import androidx.lifecycle.LiveData
import com.example.weatherapp.domain.models.CurrentWeather
import com.example.weatherapp.data.models.WeatherLocation
import com.example.weatherapp.domain.models.HourlyWeather

interface WeatherRepository {
    suspend fun getCurrentWeatherDataFromDb(): LiveData<out CurrentWeather>
    suspend fun getHourlyWeatherDataFromDb(): LiveData<out List<HourlyWeather>>
    suspend fun getWeatherLocationFromDb(): LiveData<WeatherLocation>
}