package com.example.weatherapp.ui.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapp.data.mappers.HourlyWeatherMapper
import com.example.weatherapp.data.models.HourItem
import com.example.weatherapp.data.models.HourlyWeatherResponse
import com.example.weatherapp.data.networking.ApiConfig
import com.example.weatherapp.domain.models.HourlyWeather
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HourlyWeatherViewModel : ViewModel() {
    private val apiService = ApiConfig.getApiService()
    private val hourlyWeatherData = MutableLiveData<List<HourlyWeather>>()
    private val hourlyWeatherMapper = HourlyWeatherMapper()

    fun fetchHourlyWeather(location: String) {
        apiService.getHourlyWeather(location = location)
            .enqueue(object : Callback<List<HourlyWeatherResponse>> {
            override fun onResponse(
                call: Call<List<HourlyWeatherResponse>>,
                response: Response<List<HourlyWeatherResponse>>
            ) {
                if (response.isSuccessful) {
                    val hourlyWeatherResponseList = response.body()?.get(0)
                    if (hourlyWeatherResponseList != null) {
                        // Convert the API response to the domain model HourlyWeather
                        val domainHourlyWeatherList = hourlyWeatherMapper.mapHourlyResponseToDomain(hourlyWeatherResponseList)
                        hourlyWeatherData.value = domainHourlyWeatherList
                    }
                } else {
                    // Handle API error case
                }
            }

            override fun onFailure(call: Call<List<HourlyWeatherResponse>>, t: Throwable) {
                // Handle network failure or other exceptions
            }
        })
    }

    fun getHourlyWeatherData(): LiveData<List<HourlyWeather>> {
        return hourlyWeatherData
    }
}