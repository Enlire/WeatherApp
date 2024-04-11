package com.example.weatherapp.data.repository

import androidx.lifecycle.LiveData
import com.example.weatherapp.data.daos.CurrentWeatherDao
import com.example.weatherapp.data.mappers.CurrentWeatherMapper
import com.example.weatherapp.data.models.CurrentWeatherResponse
import com.example.weatherapp.data.networking.OpenMeteoApiService
import com.example.weatherapp.data.networking.WeatherApiService
import com.example.weatherapp.domain.models.CurrentWeather
import com.example.weatherapp.ui.ErrorCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.threeten.bp.ZonedDateTime
import java.io.IOException

class WeatherRepositoryImpl(
    private val apiService: WeatherApiService,
    private val openMeteoApiService: OpenMeteoApiService,
    private val weatherDao: CurrentWeatherDao,
    private val currentWeatherMapper: CurrentWeatherMapper

) : WeatherRepository {
    private var errorCallback: ErrorCallback? = null

    override suspend fun getCurrentWeatherDataFromDb(): LiveData<CurrentWeather> {
        return withContext(Dispatchers.IO) {
            fetchCurrentWeatherData("Волгоград")
            return@withContext weatherDao.getCurrentWeather()
        }
    }

    private suspend fun fetchCurrentWeatherData(location: String): CurrentWeather? {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getCurrentWeather(location = location).execute()
                if (response.isSuccessful) {
                    val weatherResponse: CurrentWeatherResponse? = response.body()
                    weatherResponse?.let {
                        val currentWeather = currentWeatherMapper.mapCurrentResponseToDomain(it)
                        saveCurrentWeatherDataToDb(currentWeather)
                        return@withContext currentWeather
                    }
                } else {
                    errorCallback?.onError("Ошибка при получении данных о погоде. Попробуйте повторить запрос.")
                }
                null
            } catch (e: IOException) {
                errorCallback?.onError("Ошибка при выполнении запроса к серверу. Попробуйте повторить запрос.")
                null
            }
        }
    }


    private fun isFetchCurrentNeeded(lastFetchTime: ZonedDateTime, weather: CurrentWeather): Boolean {
        val thirtyMinutesAgo = ZonedDateTime.now().minusMinutes(30)
        return lastFetchTime.isBefore(thirtyMinutesAgo)
    }

    private suspend fun saveCurrentWeatherDataToDb(currentWeather: CurrentWeather) {
        withContext(Dispatchers.IO) {
            weatherDao.upsert(currentWeather)
        }
    }

    private fun setErrorCallback(callback: ErrorCallback) {
        errorCallback = callback
    }
}