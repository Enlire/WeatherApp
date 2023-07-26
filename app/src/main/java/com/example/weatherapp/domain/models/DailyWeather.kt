package com.example.weatherapp.domain.models

data class DailyWeather(
    val date: String,
    val day: String,
    val description: String,
    val iconResId: Int,
    val temp_max: Int,
    val temp_min: Int,
    val chance_of_precip: Int,
    val precip: Int,
    val wind_speed: Int,
    val uv_index: Int,
    val sunrise: String,
    val sunset: String
)
