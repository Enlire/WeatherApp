package com.example.weatherapp.data.networking

import com.example.weatherapp.data.models.CurrentWeatherResponse
import com.example.weatherapp.data.models.HourlyWeatherResponse
import retrofit2.Call
import retrofit2.http.*

interface WeatherApiService {
    @GET("current.json")
    fun getCurrentWeather(
        @Query("key") key: String = ApiConfig.API_KEY,
        @Query("q") location: String,
        @Query("Lang") languageCode: String = "ru",
        @Query("aqi") aqi: String = "no"
    ): Call<CurrentWeatherResponse>

    @GET("forecast.json")
    fun getHourlyWeather(
        @Query("key") key: String = ApiConfig.API_KEY,
        @Query("q") location: String,
        @Query("days") daysCount: Int = 2,
        @Query("aqi") aqi: String = "no",
        @Query("alerts") alerts: String = "no",
        @Query("Lang") languageCode: String = "ru"
    ): Call<HourlyWeatherResponse>
}