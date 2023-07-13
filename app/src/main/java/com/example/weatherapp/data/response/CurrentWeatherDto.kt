package com.example.weatherapp.data.response

import com.google.gson.annotations.SerializedName

data class CurrentWeatherDto(
    @SerializedName("temp_c")
    val tempC: Double,
    @SerializedName("is_day")
    val isDay: Int,
    val condition: Condition,
    @SerializedName("wind_kph")
    val windKph: Double,
    @SerializedName("wind_dir")
    val windDir: String,
    @SerializedName("pressure_in")
    val pressureIn: Double,
    val humidity: Int,
    @SerializedName("feelslike_c")
    val feelslikeC: Double,
    @SerializedName("vis_km")
    val visKm: Double,
    val uv: Double
)