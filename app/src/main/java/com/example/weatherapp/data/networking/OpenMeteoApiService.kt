package com.example.weatherapp.data.networking

import com.example.weatherapp.data.models.DailyWeatherResponse
import com.example.weatherapp.data.models.PastWeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenMeteoApiService {

    @GET("forecast")
    fun getDailyWeather(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("daily") daily: String = "weathercode,temperature_2m_max,temperature_2m_min,sunrise,sunset,uv_index_max,precipitation_sum,precipitation_probability_max,windspeed_10m_max",
        @Query("windspeed_unit") windUnit: String = "ms",
        @Query("timezone") timezone: String = "auto"
    ): Call<DailyWeatherResponse>

    //https://api.open-meteo.com/v1/forecast?latitude=48.7194&longitude=44.5018&daily=temperature_2m_max,temperature_2m_min&timezone=auto&past_days=7&forecast_days=1
    @GET("forecast")
    fun getPastWeather(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("daily") daily: String = "temperature_2m_max,temperature_2m_min",
        @Query("past_days") pastDays: Int,
        @Query("forecast_days") forecastDays: Int = 1,
        @Query("timezone") timezone: String = "auto"
    ): Call<PastWeatherResponse>
}