package com.example.weatherapp.domain.models

data class CurrentWeather (
    val location: String,
    val description: String,
    val code: Int,
    val isDay: Int,
    val icResId: Int,
    val temperature: Int,
    val feelsLike: Int,
    val windDir: String,
    val windSpeed: Int,
    val humidity: Int,
    val visibility: Int,
    val uvIndex: Int,
    val pressure: Int
)