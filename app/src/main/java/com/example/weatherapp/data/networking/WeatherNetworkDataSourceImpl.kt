package com.example.weatherapp.data.networking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.weatherapp.data.models.CurrentWeatherResponse
import com.example.weatherapp.data.models.DailyWeatherResponse
import com.example.weatherapp.data.models.HourlyWeatherResponse
import com.example.weatherapp.data.models.PastWeatherResponse
import com.example.weatherapp.ui.ErrorCallback
import java.io.IOException

class WeatherNetworkDataSourceImpl(
    private val weatherApiService: WeatherApiService,
    private val openMeteoApiService: OpenMeteoApiService
) : WeatherNetworkDataSource {
    private var errorCallback: ErrorCallback? = null

    // Current weather
    private val _downloadedCurrentWeather = MutableLiveData<CurrentWeatherResponse>()
    override val downloadedCurrentWeather: LiveData<CurrentWeatherResponse>
        get() = _downloadedCurrentWeather

    override suspend fun fetchCurrentWeather(location: String) {
        try {
            val currentWeatherResponse = weatherApiService
                .getCurrentWeather(location = location)
                .await()
            _downloadedCurrentWeather.postValue(currentWeatherResponse)
        } catch (e: IOException) {
            errorCallback?.onError("Ошибка при получении данных о погоде. Попробуйте повторить запрос.")
        }
    }

    // Hourly weather
    private val _downloadedHourlyWeather = MutableLiveData<HourlyWeatherResponse>()
    override val downloadedHourlyWeather: LiveData<HourlyWeatherResponse>
        get() = _downloadedHourlyWeather

    override suspend fun fetchHourlyWeather(location: String) {
        try {
            val hourlyWeatherResponse = weatherApiService
                .getHourlyWeather(location = location)
                .await()
            _downloadedHourlyWeather.postValue(hourlyWeatherResponse)
        } catch (e: IOException) {
            errorCallback?.onError("Ошибка при получении данных о погоде. Попробуйте повторить запрос.")
        }
    }

    // Daily weather
    private val _downloadedDailyWeather = MutableLiveData<DailyWeatherResponse>()
    override val downloadedDailyWeather: LiveData<DailyWeatherResponse>
        get() = _downloadedDailyWeather

    override suspend fun fetchDailyWeather(lat: Double, lon: Double) {
        try {
            val dailyWeatherResponse = openMeteoApiService
                .getDailyWeather(lat = lat, lon = lon)
                .await()
            _downloadedDailyWeather.postValue(dailyWeatherResponse)
        } catch (e: IOException) {
            errorCallback?.onError("Ошибка при получении данных о погоде. Попробуйте повторить запрос.")
        }
    }

    // Past weather
    private val _downloadedPastWeather = MutableLiveData<PastWeatherResponse>()
    override val downloadedPastWeather: LiveData<PastWeatherResponse>
        get() = _downloadedPastWeather

    override suspend fun fetchPastWeather(lat: Double, lon: Double) {

        try {
            val pastWeatherResponse = openMeteoApiService
                .getPastWeather(lat = lat, lon = lon, pastDays = 7)
                .await()
            _downloadedPastWeather.postValue(pastWeatherResponse)
        } catch (e: IOException) {
            errorCallback?.onError("Ошибка при получении данных о погоде. Попробуйте повторить запрос.")
        }
    }

    override fun setErrorCallback(callback: ErrorCallback) {
        errorCallback = callback
    }
}
