package com.example.weatherapp.data.networking

import com.example.weatherapp.data.models.DailyWeatherResponse
import com.example.weatherapp.data.models.PastWeatherResponse
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenMeteoApiService {

    @GET("forecast")
    fun getDailyWeather(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("daily") daily: String = "weathercode,temperature_2m_max,temperature_2m_min,sunrise,sunset,uv_index_max,precipitation_sum,precipitation_probability_max,windspeed_10m_max",
        @Query("windspeed_unit") windUnit: String = "ms",
        @Query("timezone") timezone: String = "auto"
    ): Deferred<DailyWeatherResponse>
    @GET("forecast")
    fun getPastWeather(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("daily") daily: String = "temperature_2m_max,temperature_2m_min,precipitation_sum",
        @Query("past_days") pastDays: Int,
        @Query("forecast_days") forecastDays: Int = 1,
        @Query("timezone") timezone: String = "auto"
    ): Deferred<PastWeatherResponse>

    companion object {
        operator fun invoke(): OpenMeteoApiService {
            // API response interceptor
            val loggingInterceptor = HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BASIC)

            // Client
            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()

            // Retrofit
            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.open-meteo.com/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .client(client)
                .build()

            return retrofit.create(OpenMeteoApiService::class.java)
        }
    }
}