package com.example.weatherapp.ui

import android.app.Application
import androidx.lifecycle.LiveData
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

    fun fetchWeatherData(location: String) {
        apiService.getCurrentWeather(location = location).enqueue(object : Callback<CurrentWeatherResponse> {
            override fun onResponse(call: Call<CurrentWeatherResponse>, response: Response<CurrentWeatherResponse>) {
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


//    private val _currentWeatherData = MutableLiveData<CurrentWeatherResponse>()
//    val currentWeatherData: LiveData<CurrentWeatherResponse> get() = _currentWeatherData
//
//    private val _isLoading = MutableLiveData<Boolean>()
//    val isLoading: LiveData<Boolean> get() = _isLoading
//
//    private val _isError = MutableLiveData<Boolean>()
//    val isError: LiveData<Boolean> get() = _isError
//
//    var errorMessage: String = ""
//        private set
//
//    fun getCurrentWeatherData(city: String) {
//
//        _isLoading.value = true
//        _isError.value = false
//
//        val client = ApiConfig.getApiService().getCurrentWeather(location = city)
//
//        // Send API request using Retrofit
//        client.enqueue(object : Callback<CurrentWeatherResponse> {
//
//            override fun onResponse(
//                call: Call<CurrentWeatherResponse>,
//                response: Response<CurrentWeatherResponse>) {
//                val responseBody = response.body()
//                if (!response.isSuccessful || responseBody == null) {
//                    onError("Data Processing Error")
//                    return
//                }
//
//                _isLoading.value = false
//                _currentWeatherData.postValue(responseBody)
//            }
//
//            override fun onFailure(call: Call<CurrentWeatherResponse>, t: Throwable) {
//                onError(t.message)
//                t.printStackTrace()
//            }
//
//        })
//    }
//
//    private fun onError(inputMessage: String?) {
//
//        val message = if (inputMessage.isNullOrBlank() or inputMessage.isNullOrEmpty()) "Unknown Error"
//        else inputMessage
//
//        errorMessage = StringBuilder("ERROR: ")
//            .append("$message some data may not displayed properly").toString()
//
//        _isError.value = true
//        _isLoading.value = false
//    }
}