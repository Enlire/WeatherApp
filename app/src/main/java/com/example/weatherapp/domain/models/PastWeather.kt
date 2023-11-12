package com.example.weatherapp.domain.models

data class PastWeather(
    val date: String,
    val tempMax: Float,
    val tempMin: Float,
    val presipSum: Float
)
