package com.example.weatherapp.domain.models

data class CurrentWeather (
    val location: String,
    val description: String,
    val icon: String,
    val temperature: Int,
    val feels_like: Int,
    val wind_dir: String,
    val wind_speed: Int,
    val humidity: Int,
    val visibility: Int,
    val uv_index: Int,
    val pressure: Int
)