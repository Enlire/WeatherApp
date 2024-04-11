package com.example.weatherapp.data.repository

import androidx.lifecycle.LiveData
import com.example.weatherapp.domain.models.CurrentWeather

interface WeatherRepository {
    suspend fun getCurrentWeatherDataFromDb(): LiveData<CurrentWeather>
}