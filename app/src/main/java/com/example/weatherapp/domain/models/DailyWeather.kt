package com.example.weatherapp.domain.models

data class DailyWeather(
    val date: String,
    val day: String,
    val description: String,
    val icResId: Int,
    val tempMax: Int,
    val tempMin: Int,
    val chanceOfPrecip: Int,
    val precip: Int,
    val windSpeed: Int,
    val uvIndex: Int,
    val sunrise: String,
    val sunset: String
)
