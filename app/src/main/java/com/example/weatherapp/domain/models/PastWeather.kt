package com.example.weatherapp.domain.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "past_weather")
data class PastWeather(
    @PrimaryKey(autoGenerate = false)
    var id: Int? = null,
    val location: String,
    val date: String,
    val tempMax: Float,
    val tempMin: Float,
    val presipSum: Float
)
