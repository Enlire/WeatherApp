package com.example.weatherapp.ui.viewModels

import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.domain.lazyDeferred
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: WeatherRepository
) : WeatherViewModel(repository) {

    val currentWeather by lazyDeferred {
        repository.getCurrentWeatherDataFromDb()
    }

    fun fetchCurrentWeatherData() {
        viewModelScope.launch {
            repository.getCurrentWeatherDataFromDb()
        }
    }

    /*fun fetchPastWeatherData(latitude: Double, longitude: Double) {
        openMeteoApiService.getPastWeather(latitude, longitude, pastDays=7)
            .enqueue(object : Callback<PastWeatherResponse> {
                override fun onResponse(
                    call: Call<PastWeatherResponse>,
                    response: Response<PastWeatherResponse>
                ) {
                    if (response.isSuccessful) {
                        //errorCallback?.onError(null)
                        val pastWeatherResponse: PastWeatherResponse? = response.body()
                        if (pastWeatherResponse != null) {
                            // Convert the API response to the domain model HourlyWeather
                            val pastWeatherList  = dailyWeatherMapper.mapPastResponseToDomain(pastWeatherResponse)
                            _pastWeatherList.value = pastWeatherList
                        }
                    } else {
                        //errorCallback?.onError("Ошибка при получении данных о погоде. Попробуйте повторить запрос.")
                    }
                }

                override fun onFailure(call: Call<PastWeatherResponse>, t: Throwable) {
                    //errorCallback?.onError("Ошибка при выполнении запроса к серверу. Попробуйте повторить запрос.")
                }
            })
    }

    fun setErrorCallback(callback: ErrorCallback) {
        errorCallback = callback
    }*/
}