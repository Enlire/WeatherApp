package com.example.weatherapp.data.models

import com.google.gson.annotations.SerializedName

data class PastWeatherResponse(

    @field:SerializedName("timezone")
    val timezone: String,

    @field:SerializedName("latitude")
    val latitude: Double,

    @field:SerializedName("daily")
    val daily: PastDaily,

    @field:SerializedName("utc_offset_seconds")
    val utcOffsetSeconds: Int,

    @field:SerializedName("longitude")
    val longitude: Double
)

data class PastDaily(

    @field:SerializedName("temperature_2m_max")
    val temperature2mMax: List<Float>,

    @field:SerializedName("temperature_2m_min")
    val temperature2mMin: List<Float>,

    @field:SerializedName("precipitation_sum")
    val precipitationSum: List<Float>,

    @field:SerializedName("time")
    val time: List<String>
)
