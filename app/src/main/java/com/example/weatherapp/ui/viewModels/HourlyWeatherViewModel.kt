package com.example.weatherapp.ui.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapp.data.mappers.HourlyWeatherMapper
import com.example.weatherapp.data.models.HourlyWeatherResponse
import com.example.weatherapp.data.models.TimezoneResponse
import com.example.weatherapp.data.networking.ApiConfig
import com.example.weatherapp.domain.models.HourlyWeather
import com.google.android.gms.common.api.Api
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HourlyWeatherViewModel : ViewModel() {
    private val apiService = ApiConfig.getWeatherApiService()
    private val timezoneApiService = ApiConfig.getTimezoneApiService()
    private val hourlyWeatherMapper = HourlyWeatherMapper()
    private val _hourlyWeatherList = MutableLiveData<List<HourlyWeather>>()
    val hourlyWeatherList: LiveData<List<HourlyWeather>> = _hourlyWeatherList

    fun fetchHourlyWeather(location: String, lat: Double, lon: Double) {
        fetchCurrentTime(lat, lon) { currentTime ->
            apiService.getHourlyWeather(location = location)
                .enqueue(object : Callback<HourlyWeatherResponse> {
                    override fun onResponse(
                        call: Call<HourlyWeatherResponse>,
                        response: Response<HourlyWeatherResponse>
                    ) {
                        if (response.isSuccessful) {
                            val hourlyWeatherResponse: HourlyWeatherResponse? = response.body()
                            if (hourlyWeatherResponse != null) {
                                // Convert the API response to the domain model HourlyWeather
                                val hourlyWeatherList =
                                    hourlyWeatherMapper.mapHourlyResponseToDomain(
                                        hourlyWeatherResponse,
                                        currentTime
                                    )
                                _hourlyWeatherList.value = hourlyWeatherList
                            }
                        } else {
                            // TODO: Handle API error case
                        }
                    }

                    override fun onFailure(call: Call<HourlyWeatherResponse>, t: Throwable) {
                        // TODO: Handle network failure or other exceptions
                    }
                })
        }
    }

    private fun fetchCurrentTime(lat: Double, lon: Double, callback: (String) -> Unit) {
        timezoneApiService.getTimezone(lat = lat, lon = lon)
            .enqueue(object : Callback<TimezoneResponse> {
                override fun onResponse(
                    call: Call<TimezoneResponse>,
                    response: Response<TimezoneResponse>
                ) {
                    if (response.isSuccessful) {
                        val timezoneResponse: TimezoneResponse? = response.body()
                        if (timezoneResponse != null) {
                            // Convert the API response to the domain model HourlyWeather
                            val currentTime: String = timezoneResponse.formatted
                            callback(currentTime)
                        }
                    } else {
                        // TODO: Handle API error case
                    }
                }

                override fun onFailure(call: Call<TimezoneResponse>, t: Throwable) {
                    TODO("Not yet implemented")
                }
            })
    }
}