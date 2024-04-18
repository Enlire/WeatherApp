package com.example.weatherapp.data.networking

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.weatherapp.data.mappers.CurrentWeatherMapper
import com.example.weatherapp.data.mappers.DailyWeatherMapper
import com.example.weatherapp.data.mappers.HourlyWeatherMapper
import com.example.weatherapp.data.models.CurrentWeatherResponse
import com.example.weatherapp.data.models.DailyWeatherResponse
import com.example.weatherapp.data.models.HourlyWeatherResponse
import com.example.weatherapp.data.models.PastWeatherResponse
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
    private val openMeteoApiService: OpenMeteoApiService,
    private val currentWeatherMapper: CurrentWeatherMapper,
    private val hourlyWeatherMapper: HourlyWeatherMapper,
    private val dailyWeatherMapper: DailyWeatherMapper
) : WeatherNetworkDataSource {
    private var errorCallback: ErrorCallback? = null

    private val _downloadedCurrentWeather = MutableLiveData<CurrentWeatherResponse>()
    private val _downloadedHourlyWeather = MutableLiveData<HourlyWeatherResponse>()
    private val _downloadedDailyWeather = MutableLiveData<DailyWeatherResponse>()
    private val _downloadedPastWeather = MutableLiveData<PastWeatherResponse>()
    private val _downloadedLocation = MutableLiveData<WeatherLocation>()

    override val downloadedCurrentWeather: LiveData<CurrentWeatherResponse>
        get() = _downloadedCurrentWeather
    override val downloadedHourlyWeather: LiveData<HourlyWeatherResponse>
        get() = _downloadedHourlyWeather
    override val downloadedDailyWeather: LiveData<DailyWeatherResponse>
        get() = _downloadedDailyWeather
    override val downloadedPastWeather: LiveData<PastWeatherResponse>
        get() = _downloadedPastWeather
    override val downloadedLocation: LiveData<WeatherLocation>
        get() = _downloadedLocation

    override suspend fun fetchCurrentWeather(location: String) {
        try {
            val response = weatherApiService.getCurrentWeather(location = location).execute()
            if (response.isSuccessful) {
                val weatherResponse: CurrentWeatherResponse = response.body()!!
                //weatherResponse?.let {
                    _downloadedLocation.postValue(weatherResponse.location)
                    _downloadedCurrentWeather.postValue(weatherResponse)
                //}
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
                val hourlyWeatherResponse: HourlyWeatherResponse = response.body()!!
//                hourlyWeatherResponse?.let {
//                    val hourlyWeatherList =
//                        hourlyWeatherMapper.mapHourlyResponseToDomain(
//                            hourlyWeatherResponse,
//                            hourlyWeatherResponse.location.localtime
//                        )
                    _downloadedHourlyWeather.postValue(hourlyWeatherResponse)
//                }
            } else {
                errorCallback?.onError("Ошибка при получении данных о погоде. Попробуйте повторить запрос.")
            }
        } catch (e: IOException) {
            errorCallback?.onError("Ошибка при выполнении запроса к серверу. Попробуйте повторить запрос.")
        }
    }

    override suspend fun fetchDailyWeather(lat: Double, lon: Double) {
        try {
            val response = openMeteoApiService.getDailyWeather(lat = lat, lon = lon).execute()
            if (response.isSuccessful) {
                val dailyWeatherResponse: DailyWeatherResponse = response.body()!!
//                dailyWeatherResponse?.let {
//                    val dailyWeatherList =
//                        dailyWeatherMapper.mapDailyResponseToDomain(dailyWeatherResponse)
                    _downloadedDailyWeather.postValue(dailyWeatherResponse)
//                }
            } else {
                errorCallback?.onError("Ошибка при получении данных о погоде. Попробуйте повторить запрос.")
            }
        } catch (e: IOException) {
            errorCallback?.onError("Ошибка при выполнении запроса к серверу. Попробуйте повторить запрос.")
        }
    }

    override suspend fun fetchPastWeather(lat: Double, lon: Double) {

        try {
            val response = openMeteoApiService.getPastWeather(lat = lat, lon = lon, pastDays = 7).execute()
            if (response.isSuccessful) {
                val pastWeatherResponse: PastWeatherResponse = response.body()!!
//                pastWeatherResponse?.let {
//                    val pastWeatherList =
//                        dailyWeatherMapper.mapPastResponseToDomain(pastWeatherResponse)
                    _downloadedPastWeather.postValue(pastWeatherResponse)
//                }
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
