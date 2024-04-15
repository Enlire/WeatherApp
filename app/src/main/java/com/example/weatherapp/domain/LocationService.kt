package com.example.weatherapp.domain

import com.example.weatherapp.data.models.WeatherLocation

interface LocationService {
    suspend fun hasLocationChanged(lastWeatherLocation: WeatherLocation): Boolean
    suspend fun getPreferredLocation(): String//Triple<Double, Double, String>
}