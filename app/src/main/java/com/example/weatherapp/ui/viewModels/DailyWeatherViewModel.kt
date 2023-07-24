package com.example.weatherapp.ui.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapp.data.mappers.DailyWeatherMapper
import com.example.weatherapp.data.models.DailyWeatherResponse
import com.example.weatherapp.data.networking.ApiConfig
import com.example.weatherapp.domain.models.DailyWeather
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DailyWeatherViewModel : ViewModel() {
    private val apiService = ApiConfig.getOpenMeteoApiService()
    private val dailyWeatherMapper = DailyWeatherMapper()
    private val _dailyWeatherList = MutableLiveData<List<DailyWeather>>()
    val dailyWeatherList: LiveData<List<DailyWeather>> = _dailyWeatherList

    fun fetchHourlyWeather() {
        apiService.getDailyWeather(lat = 48.7194, lon = 44.5018)
            .enqueue(object : Callback<DailyWeatherResponse> {
                override fun onResponse(
                    call: Call<DailyWeatherResponse>,
                    response: Response<DailyWeatherResponse>
                ) {
                    if (response.isSuccessful) {
                        val dailyWeatherResponse: DailyWeatherResponse? = response.body()
                        if (dailyWeatherResponse != null) {
                            // Convert the API response to the domain model HourlyWeather
                            val hourlyWeatherList  = dailyWeatherMapper.mapDailyResponseToDomain(dailyWeatherResponse)
                            _dailyWeatherList.value = hourlyWeatherList
                        }
                    } else {
                        // TODO: Handle API error case
                    }
                }

                override fun onFailure(call: Call<DailyWeatherResponse>, t: Throwable) {
                    // TODO: Handle network failure or other exceptions
                }
            })
    }
}