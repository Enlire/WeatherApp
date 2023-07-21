package com.example.weatherapp.ui.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapp.data.mappers.HourlyWeatherMapper
import com.example.weatherapp.data.models.HourlyWeatherResponse
import com.example.weatherapp.data.networking.ApiConfig
import com.example.weatherapp.domain.models.HourlyWeather
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HourlyWeatherViewModel : ViewModel() {
    private val apiService = ApiConfig.getApiService()
    private val hourlyWeatherMapper = HourlyWeatherMapper()
    private val _hourlyWeatherList = MutableLiveData<List<HourlyWeather>>()
    val hourlyWeatherList: LiveData<List<HourlyWeather>> = _hourlyWeatherList

    fun fetchHourlyWeather(location: String) {
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
                        val hourlyWeatherList  = hourlyWeatherMapper.mapHourlyResponseToDomain(hourlyWeatherResponse)
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