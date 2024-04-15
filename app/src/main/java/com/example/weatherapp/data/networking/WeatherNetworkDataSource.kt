package com.example.weatherapp.data.networking

import androidx.lifecycle.LiveData
import com.example.weatherapp.data.models.WeatherLocation
import com.example.weatherapp.domain.models.CurrentWeather
import com.example.weatherapp.domain.models.DailyWeather
import com.example.weatherapp.domain.models.HourlyWeather
import com.example.weatherapp.domain.models.PastWeather
import com.example.weatherapp.ui.ErrorCallback

interface WeatherNetworkDataSource {
    val downloadedCurrentWeather: LiveData<CurrentWeather>
    val downloadedHourlyWeather: LiveData<List<HourlyWeather>>
    val downloadedDailyWeather: LiveData<DailyWeather>
    val downloadedPastWeather: LiveData<PastWeather>

    suspend fun fetchCurrentWeather(location: String)
    suspend fun fetchHourlyWeather(location: String)
    fun setErrorCallback(callback: ErrorCallback)
}