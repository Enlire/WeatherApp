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
    val chance_of_rain: Int,
    val chance_of_snow: Int,
    val precip: Int,
    val cloud: Int,
    val dew_point: Int,
    val wind_dir: String,
    val wind_speed: Int,
    val humidity: Int,
    val visibility: Int,
    val uv_index: Int,
    val pressure: Int
)
