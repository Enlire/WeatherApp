package com.example.weatherapp.data.models

import com.google.gson.annotations.SerializedName

data class TimezoneResponse(

    @field:SerializedName("status")
    val status: String,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("formatted")
    val formatted: String
)