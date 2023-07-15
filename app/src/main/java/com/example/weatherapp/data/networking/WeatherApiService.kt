package com.example.weatherapp.data.networking

import com.example.weatherapp.data.models.CurrentWeatherResponse
import retrofit2.Call
import retrofit2.http.*

interface WeatherApiService {
    // Get current weather data
    @GET("current.json")
    fun getCurrentWeather(
        @Query("key") key: String = ApiConfig.API_KEY,
        @Query("q") location: String,
        @Query("Lang") languageCode: String = "ru",
        @Query("aqi") aqi: String = "no"
    ): Call<CurrentWeatherResponse>
}