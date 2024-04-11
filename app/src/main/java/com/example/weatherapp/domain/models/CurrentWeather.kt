package com.example.weatherapp.domain.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

const val CURRENT_WEATHER_ID = 0

@Entity(tableName = "current_weather")
data class CurrentWeather (
    @PrimaryKey
    val id: Int = CURRENT_WEATHER_ID,
    //@ColumnInfo(name = "location_name")
    val location: String,
    //@ColumnInfo(name = "location_localtime")
    val localtime: String,
    //@ColumnInfo(name = "current_lastUpdated")
    val lastUpdated: String,
    //@ColumnInfo(name = "current_condition_text")
    val description: String,
    //@ColumnInfo(name = "current_condition_code")
    val code: Int,
    //@ColumnInfo(name = "current_isDay")
    val isDay: Int,
    //@ColumnInfo(name = "current_icResId")
    val icResId: Int,
    //@ColumnInfo(name = "current_tempC")
    val temperature: Int,
    //@ColumnInfo(name = "current_feelslikeC")
    val feelsLike: Int,
    //@ColumnInfo(name = "current_windDir")
    val windDir: String,
    //@ColumnInfo(name = "current_windKph")
    val windSpeed: Int,
    //@ColumnInfo(name = "current_humidity")
    val humidity: Int,
    //@ColumnInfo(name = "current_visKm")
    val visibility: Int,
    //@ColumnInfo(name = "current_uv")
    val uvIndex: Int,
    //@ColumnInfo(name = "current_pressureIn")
    val pressure: Int
)