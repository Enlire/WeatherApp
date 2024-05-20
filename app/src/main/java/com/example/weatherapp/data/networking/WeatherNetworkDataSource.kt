package com.example.weatherapp.data.networking

import androidx.lifecycle.LiveData
import com.example.weatherapp.data.models.CurrentWeatherResponse
import com.example.weatherapp.data.models.DailyWeatherResponse
import com.example.weatherapp.data.models.HourlyWeatherResponse
import com.example.weatherapp.data.models.PastWeatherResponse
import com.example.weatherapp.ui.ErrorCallback

interface WeatherNetworkDataSource {
    val downloadedCurrentWeather: LiveData<CurrentWeatherResponse>
    val downloadedHourlyWeather: LiveData<HourlyWeatherResponse>
    val downloadedDailyWeather: LiveData<DailyWeatherResponse>
    val downloadedPastWeather: LiveData<PastWeatherResponse>

    suspend fun fetchCurrentWeather(location: String)
    suspend fun fetchHourlyWeather(location: String)
    suspend fun fetchDailyWeather(lat: Double, lon: Double)
    suspend fun fetchPastWeather(lat: Double, lon: Double)
    fun resetErrorCount()
    fun setErrorCallback(callback: ErrorCallback)
}