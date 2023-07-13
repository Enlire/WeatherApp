package com.example.weatherapp.data.response

import com.google.gson.annotations.SerializedName


data class CurrentWeatherApi(
    val location: Location,
    @SerializedName("current")
    val currentWeatherDto: CurrentWeatherDto
)