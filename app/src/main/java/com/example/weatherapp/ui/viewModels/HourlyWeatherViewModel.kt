package com.example.weatherapp.ui.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import com.example.weatherapp.data.mappers.HourlyWeatherMapper
import com.example.weatherapp.data.models.HourlyWeatherResponse
import com.example.weatherapp.data.models.TimezoneResponse
import com.example.weatherapp.data.networking.ApiConfig
import com.example.weatherapp.domain.models.HourlyWeather
import com.example.weatherapp.ui.ErrorCallback
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HourlyWeatherViewModel : ViewModel() {
    private val apiService = ApiConfig.getWeatherApiService()
    private val timezoneApiService = ApiConfig.getTimezoneApiService()
    private val hourlyWeatherMapper = HourlyWeatherMapper()
    private val _hourlyWeatherList = MutableLiveData<List<HourlyWeather>>()
    val hourlyWeatherList: LiveData<List<HourlyWeather>> = _hourlyWeatherList
    private var errorCallback: ErrorCallback? = null

    fun fetchHourlyWeather(location: String, lat: Double, lon: Double) {
        fetchCurrentTime(lat, lon) { currentTime ->
            apiService.getHourlyWeather(location = location)
                .enqueue(object : Callback<HourlyWeatherResponse> {
                    override fun onResponse(
                        call: Call<HourlyWeatherResponse>,
                        response: Response<HourlyWeatherResponse>
                    ) {
                        if (response.isSuccessful) {
                            errorCallback?.onError(null)
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
                            errorCallback?.onError("Ошибка при получении данных о погоде. Попробуйте повторить запрос.")
                        }
                    }

                    override fun onFailure(call: Call<HourlyWeatherResponse>, t: Throwable) {
                        errorCallback?.onError("Ошибка при выполнении запроса к серверу. Попробуйте повторить запрос.")
                    }
                })
        }
    }

    private fun fetchCurrentTime(lat: Double, lon: Double, callback: (String) -> Unit) {
        var currentTime: String
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
                            currentTime = timezoneResponse.formatted
                            callback(currentTime)
                        }
                    } else {
                        currentTime = "00:00"
                    }
                }

                override fun onFailure(call: Call<TimezoneResponse>, t: Throwable) {
                    currentTime = "00:00"
                }
            })
    }

    fun setErrorCallback(callback: ErrorCallback) {
        errorCallback = callback
    }
}