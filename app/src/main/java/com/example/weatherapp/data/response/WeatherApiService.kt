package com.example.weatherapp.data.response

import retrofit2.http.GET
import retrofit2.http.Query

const val API_KEY = "50a3be20b7454b1482d113119232303"

interface WeatherApiService {

    @GET("current.json")
    fun getCurrentWeather(
        @Query("q") location: String
    ): CurrentWeatherDto
}