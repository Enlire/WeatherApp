package com.example.weatherapp.data.networking

import com.example.weatherapp.data.models.CurrentWeatherResponse
import com.example.weatherapp.data.models.HourlyWeatherResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

const val API_KEY = "50a3be20b7454b1482d113119232303"

interface WeatherApiService {
    @GET("current.json")
    fun getCurrentWeather(
        @Query("key") key: String = API_KEY,
        @Query("q") location: String,
        @Query("Lang") languageCode: String = "ru",
        @Query("aqi") aqi: String = "no"
    ): Call<CurrentWeatherResponse>

    @GET("forecast.json")
    fun getHourlyWeather(
        @Query("key") key: String = API_KEY,
        @Query("q") location: String,
        @Query("days") daysCount: Int = 2,
        @Query("aqi") aqi: String = "no",
        @Query("alerts") alerts: String = "no",
        @Query("Lang") languageCode: String = "ru"
    ): Call<HourlyWeatherResponse>

    companion object {
        operator fun invoke(): WeatherApiService {
            // API response interceptor
            val loggingInterceptor = HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY)

            // Client
            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()

            // Retrofit
            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.weatherapi.com/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                //.client(client)
                .build()

            return retrofit.create(WeatherApiService::class.java)
        }
    }
}