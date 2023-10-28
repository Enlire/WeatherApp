package com.example.weatherapp.ui.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapp.data.mappers.CurrentWeatherMapper
import com.example.weatherapp.data.mappers.DailyWeatherMapper
import com.example.weatherapp.data.models.CurrentWeatherResponse
import com.example.weatherapp.data.models.DailyWeatherResponse
import com.example.weatherapp.data.models.PastWeatherResponse
import com.example.weatherapp.data.networking.ApiConfig
import com.example.weatherapp.domain.models.CurrentWeather
import com.example.weatherapp.domain.models.DailyWeather
import com.example.weatherapp.domain.models.PastWeather
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel() : ViewModel() {

    private val apiService = ApiConfig.getWeatherApiService()
    private val openMeteoApiService = ApiConfig.getOpenMeteoApiService()
    val weatherData: MutableLiveData<CurrentWeather> = MutableLiveData()
    private val currentWeatherMapper = CurrentWeatherMapper()
    private val dailyWeatherMapper = DailyWeatherMapper()
    private val _pastWeatherList = MutableLiveData<List<PastWeather>>()
    val pastWeatherList: LiveData<List<PastWeather>> = _pastWeatherList

    fun fetchCurrentWeatherData(location: String) {
        apiService.getCurrentWeather(location = location)
            .enqueue(object : Callback<CurrentWeatherResponse> {
            override fun onResponse(
                call: Call<CurrentWeatherResponse>,
                response: Response<CurrentWeatherResponse>
            ) {
                if (response.isSuccessful) {
                    val weatherResponse: CurrentWeatherResponse? = response.body()
                    if (weatherResponse != null) {

                    }
                    weatherResponse?.let {
                        val currentWeather = currentWeatherMapper.mapCurrentResponseToDomain(it)
                        weatherData.postValue(currentWeather)
                    }
                } else {
                    // TODO: You can post an error state to the weatherData LiveData here
                }
            }

                override fun onFailure(call: Call<CurrentWeatherResponse>, t: Throwable) {
                    // TODO: You can post an error state to the weatherData LiveData here
                }
            })
    }

    fun fetchPastWeatherData(latitude: Double, longitude: Double) {
        openMeteoApiService.getPastWeather(latitude, longitude)
            .enqueue(object : Callback<PastWeatherResponse> {
                override fun onResponse(
                    call: Call<PastWeatherResponse>,
                    response: Response<PastWeatherResponse>
                ) {
                    if (response.isSuccessful) {
                        val pastWeatherResponse: PastWeatherResponse? = response.body()
                        if (pastWeatherResponse != null) {
                            // Convert the API response to the domain model HourlyWeather
                            val pastWeatherList  = dailyWeatherMapper.mapPastResponseToDomain(pastWeatherResponse)
                            _pastWeatherList.value = pastWeatherList
                        }
                    } else {
                        // TODO: Handle API error case
                    }
                }

                override fun onFailure(call: Call<PastWeatherResponse>, t: Throwable) {
                    TODO("Not yet implemented")
                }
            })
    }
}