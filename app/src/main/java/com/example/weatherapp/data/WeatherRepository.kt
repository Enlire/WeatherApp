package com.example.weatherapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.weatherapp.data.mappers.CurrentWeatherMapper
import com.example.weatherapp.data.mappers.HourlyWeatherMapper
import com.example.weatherapp.data.models.CurrentWeatherResponse
import com.example.weatherapp.data.models.HourlyWeatherResponse
import com.example.weatherapp.data.networking.ApiConfig
import com.example.weatherapp.domain.models.CurrentWeather
import com.example.weatherapp.domain.models.HourlyWeather
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherRepository {

    private val apiService = ApiConfig.getWeatherApiService()

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
                            val currentWeatherMapper = CurrentWeatherMapper()
                            val currentWeather = currentWeatherMapper.mapCurrentResponseToDomain(it)
                            val weatherData: MutableLiveData<CurrentWeather> = MutableLiveData()
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

    fun fetchHourlyWeather(location: String) {
        val _hourlyWeatherList = MutableLiveData<List<HourlyWeather>>()
        val hourlyWeatherList: LiveData<List<HourlyWeather>> = _hourlyWeatherList

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
                            val hourlyWeatherMapper = HourlyWeatherMapper()
                            val hourlyWeatherList  = hourlyWeatherMapper.mapHourlyResponseToDomain(hourlyWeatherResponse)
                            _hourlyWeatherList.value = hourlyWeatherList
                        }
                    } else {
                        // Handle API error case
                    }
                }

                override fun onFailure(call: Call<HourlyWeatherResponse>, t: Throwable) {
                    // Handle network failure or other exceptions
                }
            })
    }
}