package com.example.weatherapp.ui.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import com.example.weatherapp.data.mappers.DailyWeatherMapper
import com.example.weatherapp.data.models.DailyWeatherResponse
import com.example.weatherapp.data.networking.ApiConfig
import com.example.weatherapp.domain.models.DailyWeather
import com.example.weatherapp.ui.ErrorCallback
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DailyWeatherViewModel : ViewModel() {
    private val apiService = ApiConfig.getOpenMeteoApiService()
    private val dailyWeatherMapper = DailyWeatherMapper()
    private val _dailyWeatherList = MutableLiveData<List<DailyWeather>>()
    val dailyWeatherList: LiveData<List<DailyWeather>> = _dailyWeatherList
    private var errorCallback: ErrorCallback? = null

    fun fetchDailyWeather(latitude: Double, longitude: Double) {
        apiService.getDailyWeather(latitude, longitude)
            .enqueue(object : Callback<DailyWeatherResponse> {
                override fun onResponse(
                    call: Call<DailyWeatherResponse>,
                    response: Response<DailyWeatherResponse>
                ) {
                    if (response.isSuccessful) {
                        errorCallback?.onError(null)
                        val dailyWeatherResponse: DailyWeatherResponse? = response.body()
                        if (dailyWeatherResponse != null) {
                            // Convert the API response to the domain model HourlyWeather
                            val dailyWeatherList  = dailyWeatherMapper.mapDailyResponseToDomain(dailyWeatherResponse)
                            _dailyWeatherList.value = dailyWeatherList
                        }
                    } else {
                        errorCallback?.onError("Ошибка при получении данных о погоде. Попробуйте повторить запрос.")
                    }
                }

                override fun onFailure(call: Call<DailyWeatherResponse>, t: Throwable) {
                    errorCallback?.onError("Ошибка при выполнении запроса к серверу. Попробуйте повторить запрос.")
                }
            })
    }

    fun setErrorCallback(callback: ErrorCallback) {
        errorCallback = callback
    }
}