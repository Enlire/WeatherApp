package com.example.weatherapp.data.models

import com.google.gson.annotations.SerializedName

data class PastWeatherResponse(

    @field:SerializedName("elevation")
    val elevation: Any,

    @field:SerializedName("generationtime_ms")
    val generationtimeMs: Any,

    @field:SerializedName("timezone_abbreviation")
    val timezoneAbbreviation: String,

    @field:SerializedName("timezone")
    val timezone: String,

    @field:SerializedName("latitude")
    val latitude: Any,

    @field:SerializedName("daily")
    val daily: PastDaily,

    @field:SerializedName("utc_offset_seconds")
    val utcOffsetSeconds: Int,

    @field:SerializedName("longitude")
    val longitude: Any
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
