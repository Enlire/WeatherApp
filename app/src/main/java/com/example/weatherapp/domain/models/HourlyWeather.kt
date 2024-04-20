package com.example.weatherapp.domain.models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "hourly_weather", indices = [Index (value = ["date"], unique = true)])
data class HourlyWeather(
    @PrimaryKey(autoGenerate = false)
    var id: Int? = null,
    val location: String,
    val description: String,
    val date: String,
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
