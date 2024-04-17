package com.example.weatherapp.domain.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.weatherapp.data.models.WeatherLocation

const val CURRENT_WEATHER_ID = 0

@Entity(tableName = "current_weather")
data class CurrentWeather(
    @PrimaryKey(autoGenerate = false)
    var id: Int = CURRENT_WEATHER_ID,
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