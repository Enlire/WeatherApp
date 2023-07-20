package com.example.weatherapp.ui.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapp.data.mappers.CurrentWeatherMapper
import com.example.weatherapp.data.models.CurrentWeatherResponse
import com.example.weatherapp.data.networking.ApiConfig
import com.example.weatherapp.domain.models.CurrentWeather
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel : ViewModel() {
    private val apiService = ApiConfig.getApiService()
    val weatherData: MutableLiveData<CurrentWeather> = MutableLiveData()
    private val currentWeatherMapper = CurrentWeatherMapper()

    fun fetchCurrentWeatherData(location: String) {
        apiService.getCurrentWeather(location = location)
            .enqueue(object : Callback<CurrentWeatherResponse> {
            override fun onResponse(
                call: Call<CurrentWeatherResponse>,
                response: Response<CurrentWeatherResponse>
            ) {
                if (response.isSuccessful) {
                    val weatherResponse = response.body()
                    weatherResponse?.let {
                        val currentWeather = currentWeatherMapper.mapCurrentResponseToDomain(it)
                        weatherData.postValue(currentWeather)
                    }
                } else {
                    // Handle API error case
                    // You can post an error state to the weatherData LiveData here
                }
            }

                override fun onFailure(call: Call<CurrentWeatherResponse>, t: Throwable) {
                    // Handle network failure or other exceptions
                    // You can post an error state to the weatherData LiveData here
                }
            })
    }
}