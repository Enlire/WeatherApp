package com.example.weatherapp.domain.models

data class HourlyWeather(
    val location: String,
    val description: String,
    val day: String,
    val hour: String,
    val isDay: Int,
    val code: Int,
    val icResId: Int,
    val temperature: Int,
    val chanceOfRain: Int,
    val chanceOfSnow: Int,
    val precip: Int,
    val cloud: Int,
    val dewPoint: Int,
    val windDir: String,
    val windSpeed: Int,
    val humidity: Int,
    val visibility: Int,
    val uvIndex: Int,
    val pressure: Int
)
