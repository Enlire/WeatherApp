package com.example.weatherapp.data.networking

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.weatherapp.data.mappers.CurrentWeatherMapper
import com.example.weatherapp.data.mappers.HourlyWeatherMapper
import com.example.weatherapp.data.models.CurrentWeatherResponse
import com.example.weatherapp.data.models.HourlyWeatherResponse
import com.example.weatherapp.data.models.WeatherLocation
import com.example.weatherapp.domain.models.CurrentWeather
import com.example.weatherapp.domain.models.DailyWeather
import com.example.weatherapp.domain.models.HourlyWeather
import com.example.weatherapp.domain.models.PastWeather
import com.example.weatherapp.ui.ErrorCallback
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.security.PrivateKey

class WeatherNetworkDataSourceImpl(
    private val weatherApiService: WeatherApiService,
    private val currentWeatherMapper: CurrentWeatherMapper,
    private val hourlyWeatherMapper: HourlyWeatherMapper
) : WeatherNetworkDataSource {
    private var errorCallback: ErrorCallback? = null

    private val _downloadedCurrentWeather = MutableLiveData<CurrentWeather>()
    private val _downloadedHourlyWeather = MutableLiveData<List<HourlyWeather>>()
    private val _downloadedDailyWeather = MutableLiveData<DailyWeather>()
    private val _downloadedPastWeather = MutableLiveData<PastWeather>()

    override val downloadedCurrentWeather: LiveData<CurrentWeather>
        get() = _downloadedCurrentWeather
    override val downloadedHourlyWeather: LiveData<List<HourlyWeather>>
        get() = _downloadedHourlyWeather
    override val downloadedDailyWeather: LiveData<DailyWeather>
        get() = _downloadedDailyWeather
    override val downloadedPastWeather: LiveData<PastWeather>
        get() = _downloadedPastWeather

    override suspend fun fetchCurrentWeather(location: String) {
        try {
            val response = weatherApiService.getCurrentWeather(location = location).execute()
            if (response.isSuccessful) {
                val weatherResponse: CurrentWeatherResponse? = response.body()
                weatherResponse?.let {
                    val currentWeather = currentWeatherMapper.mapCurrentResponseToDomain(it)
                    _downloadedCurrentWeather.postValue(currentWeather)
                }
            } else {
                errorCallback?.onError("Ошибка при получении данных о погоде. Попробуйте повторить запрос.")
            }
        } catch (e: IOException) {
            errorCallback?.onError("Ошибка при выполнении запроса к серверу. Попробуйте повторить запрос.")
        }
    }

    override suspend fun fetchHourlyWeather(location: String) {
        try {
            val response = weatherApiService.getHourlyWeather(location = location).execute()
            if (response.isSuccessful) {
                val hourlyWeatherResponse: HourlyWeatherResponse? = response.body()
                hourlyWeatherResponse?.let {
                    val hourlyWeatherList =
                        hourlyWeatherMapper.mapHourlyResponseToDomain(
                            hourlyWeatherResponse,
                            hourlyWeatherResponse.location.localtime
                        )
                    Log.d("hourly response", hourlyWeatherList.toString())
                    _downloadedHourlyWeather.postValue(hourlyWeatherList)
                }
            } else {
                errorCallback?.onError("Ошибка при получении данных о погоде. Попробуйте повторить запрос.")
            }
        } catch (e: IOException) {
            errorCallback?.onError("Ошибка при выполнении запроса к серверу. Попробуйте повторить запрос.")
        }
    }

    override fun setErrorCallback(callback: ErrorCallback) {
        errorCallback = callback
    }
}