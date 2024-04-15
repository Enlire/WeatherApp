package com.example.weatherapp.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.weatherapp.data.daos.CurrentWeatherDao
import com.example.weatherapp.data.daos.HourlyWeatherDao
import com.example.weatherapp.data.daos.WeatherLocationDao
import com.example.weatherapp.data.models.CurrentWeatherResponse
import com.example.weatherapp.data.networking.OpenMeteoApiService
import com.example.weatherapp.data.networking.WeatherApiService
import com.example.weatherapp.domain.models.CurrentWeather
import com.example.weatherapp.data.models.WeatherLocation
import com.example.weatherapp.data.networking.WeatherNetworkDataSource
import com.example.weatherapp.domain.LocationService
import com.example.weatherapp.domain.models.HourlyWeather
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.ZonedDateTime

class WeatherRepositoryImpl(
    private val currentWeatherDao: CurrentWeatherDao,
    private val hourlyWeatherDao: HourlyWeatherDao,
    private val weatherLocationDao: WeatherLocationDao,
    private val weatherNetworkDataSource: WeatherNetworkDataSource,
    private val locationService: LocationService

) : WeatherRepository {
    init {
        weatherNetworkDataSource.apply {
            downloadedCurrentWeather.observeForever { newCurrentWeather ->
                persistFetchedCurrentWeather(newCurrentWeather)
            }
            downloadedHourlyWeather.observeForever {newHourlyWeather ->
                persistFetchedHourlyWeather(newHourlyWeather)
            }
        }
    }

    override suspend fun getCurrentWeatherDataFromDb(): LiveData<out CurrentWeather> {
        return withContext(Dispatchers.IO) {
            initWeatherData()
            return@withContext currentWeatherDao.getCurrentWeather()
        }
    }

    override suspend fun getHourlyWeatherDataFromDb(): LiveData<out List<HourlyWeather>> {
        return withContext(Dispatchers.IO) {
            initWeatherData()
            return@withContext hourlyWeatherDao.getHourlyWeather()
        }
    }

    override suspend fun getWeatherLocationFromDb(): LiveData<WeatherLocation> {
        return withContext(Dispatchers.IO) {
            return@withContext weatherLocationDao.getLocation()
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun persistFetchedCurrentWeather(fetchedCurrentWeather: CurrentWeather) {
        GlobalScope.launch(Dispatchers.IO) {
            currentWeatherDao.upsert(fetchedCurrentWeather)
            weatherLocationDao.upsert(fetchedCurrentWeather.location)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun persistFetchedHourlyWeather(fetchedHourlyWeather: List<HourlyWeather>) {
        GlobalScope.launch(Dispatchers.IO) {
            hourlyWeatherDao.deleteAllHourlyWeather()
            hourlyWeatherDao.upsert(fetchedHourlyWeather)
            //weatherLocationDao.upsert(fetchedHourlyWeather.)
        }
    }

    private suspend fun initWeatherData() {
        val lastWeatherLocation = weatherLocationDao.getLocationNonLive()

        if (lastWeatherLocation == null
            || locationService.hasLocationChanged(lastWeatherLocation)) {
            fetchCurrentWeather()
            fetchHourlyWeather()
            return
        }

        if (isFetchCurrentNeeded(lastWeatherLocation.zonedDateTime))
            fetchCurrentWeather()
    }

    private suspend fun fetchCurrentWeather() {
        weatherNetworkDataSource.fetchCurrentWeather(
            locationService.getPreferredLocation()
        )
    }

    private suspend fun fetchHourlyWeather() {
        weatherNetworkDataSource.fetchHourlyWeather(
            locationService.getPreferredLocation()
        )
    }

    private fun isFetchCurrentNeeded(lastFetchTime: ZonedDateTime): Boolean {
        val thirtyMinutesAgo = ZonedDateTime.now().minusMinutes(1)
        return lastFetchTime.isBefore(thirtyMinutesAgo)
    }
}