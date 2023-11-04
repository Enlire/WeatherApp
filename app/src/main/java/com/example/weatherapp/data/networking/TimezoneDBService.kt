package com.example.weatherapp.data.networking

import com.example.weatherapp.data.models.TimezoneResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TimezoneDBService {
    @GET("get-time-zone")
    fun getTimezone(
        @Query("key") key: String = ApiConfig.TIMEZONE_API_KEY,
        @Query("lat") lat: Double,
        @Query("lng") lon: Double,
        @Query("format") format: String = "json",
        @Query("fields") fields: String = "formatted",
        @Query("by") by: String = "position",

    ): Call<TimezoneResponse>
}