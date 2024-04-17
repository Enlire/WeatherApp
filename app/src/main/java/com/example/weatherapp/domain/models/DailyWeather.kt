package com.example.weatherapp.domain.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_weather")
data class DailyWeather(
    @PrimaryKey(autoGenerate = false)
    var id: Int? = null,
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
