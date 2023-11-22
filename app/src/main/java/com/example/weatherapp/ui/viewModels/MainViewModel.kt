package com.example.weatherapp.ui.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapp.data.mappers.CurrentWeatherMapper
import com.example.weatherapp.data.mappers.DailyWeatherMapper
import com.example.weatherapp.data.models.CurrentWeatherResponse
import com.example.weatherapp.data.models.PastWeatherResponse
import com.example.weatherapp.data.networking.ApiConfig
import com.example.weatherapp.domain.models.CurrentWeather
import com.example.weatherapp.domain.models.PastWeather
import com.example.weatherapp.ui.ErrorCallback
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel : ViewModel() {
    private val apiService = ApiConfig.getWeatherApiService()
    private val openMeteoApiService = ApiConfig.getOpenMeteoApiService()
    val weatherData: MutableLiveData<CurrentWeather> = MutableLiveData()
    private val currentWeatherMapper = CurrentWeatherMapper()
    private val dailyWeatherMapper = DailyWeatherMapper()
    private val _pastWeatherList = MutableLiveData<List<PastWeather>>()
    val pastWeatherList: LiveData<List<PastWeather>> = _pastWeatherList
    private var errorCallback: ErrorCallback? = null

    fun fetchCurrentWeatherData(location: String) {
        apiService.getCurrentWeather(location = location)
            .enqueue(object : Callback<CurrentWeatherResponse> {
                override fun onResponse(
                    call: Call<CurrentWeatherResponse>,
                    response: Response<CurrentWeatherResponse>
                ) {
                    if (response.isSuccessful) {
                        val weatherResponse: CurrentWeatherResponse? = response.body()
                        weatherResponse?.let {
                            val currentWeather = currentWeatherMapper.mapCurrentResponseToDomain(it)
                            weatherData.postValue(currentWeather)
                        }
                    } else {
                        errorCallback?.onError("Ошибка при получении данных о погоде. Попробуйте повторить запрос.")
                    }
                }

                override fun onFailure(call: Call<CurrentWeatherResponse>, t: Throwable) {
                    //errorCallback?.onError("Ошибка при выполнении запроса к серверу. Попробуйте повторить запрос.")
                }
            })
    }

    fun fetchPastWeatherData(latitude: Double, longitude: Double) {
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
    }
}