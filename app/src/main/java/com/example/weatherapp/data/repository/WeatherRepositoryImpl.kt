package com.example.weatherapp.data.repository

import androidx.lifecycle.LiveData
import com.example.weatherapp.data.daos.CurrentWeatherDao
import com.example.weatherapp.data.daos.DailyWeatherDao
import com.example.weatherapp.data.daos.HourlyWeatherDao
import com.example.weatherapp.data.daos.PastWeatherDao
import com.example.weatherapp.data.daos.WeatherLocationDao
import com.example.weatherapp.domain.models.CurrentWeather
import com.example.weatherapp.data.models.WeatherLocation
import com.example.weatherapp.data.networking.WeatherNetworkDataSource
import com.example.weatherapp.domain.LocationService
import com.example.weatherapp.domain.models.DailyWeather
import com.example.weatherapp.domain.models.HourlyWeather
import com.example.weatherapp.domain.models.PastWeather
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.ZonedDateTime

class WeatherRepositoryImpl(
    private val currentWeatherDao: CurrentWeatherDao,
    private val hourlyWeatherDao: HourlyWeatherDao,
    private val dailyWeatherDao: DailyWeatherDao,
    private val pastWeatherDao: PastWeatherDao,
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
            downloadedLocation.observeForever { newLocation ->
                persistFetchedLocation(newLocation)
            }
            downloadedDailyWeather.observeForever { newDailyWeather ->
                persistFetchedDailyWeather(newDailyWeather)
            }
            downloadedPastWeather.observeForever { newPastWeather ->
                persistFetchedPastWeather(newPastWeather)
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

    override suspend fun getDailyWeatherDataFromDb(): LiveData<out List<DailyWeather>> {
        return withContext(Dispatchers.IO) {
            initWeatherData()
            return@withContext dailyWeatherDao.getDailyWeather()
        }
    }

    override suspend fun getPastWeatherFromDb(): LiveData<out List<PastWeather>> {
        return withContext(Dispatchers.IO) {
            initWeatherData()
            return@withContext pastWeatherDao.getPastWeather()
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
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun persistFetchedLocation(fetchedLocation: WeatherLocation) {
        GlobalScope.launch(Dispatchers.IO) {
            weatherLocationDao.upsert(fetchedLocation)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun persistFetchedHourlyWeather(fetchedHourlyWeather: List<HourlyWeather>) {
        GlobalScope.launch(Dispatchers.IO) {
            hourlyWeatherDao.deleteAllHourlyWeather()
            hourlyWeatherDao.upsert(fetchedHourlyWeather)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun persistFetchedDailyWeather(fetchedDailyWeather: List<DailyWeather>) {
        GlobalScope.launch(Dispatchers.IO) {
            dailyWeatherDao.deleteAllDailyWeather()
            dailyWeatherDao.upsert(fetchedDailyWeather)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun persistFetchedPastWeather(fetchedPastWeather: List<PastWeather>) {
        GlobalScope.launch(Dispatchers.IO) {
            pastWeatherDao.deleteAllPastWeather()
            pastWeatherDao.upsert(fetchedPastWeather)
        }
    }

    private suspend fun initWeatherData() {
        val lastWeatherLocation = weatherLocationDao.getLocationNonLive()

        if (lastWeatherLocation == null
            || locationService.hasLocationChanged(lastWeatherLocation)) {
            fetchCurrentWeather()
            fetchHourlyWeather()
            fetchDailyWeather()
            fetchPastWeather()
            return
        }

        if (isFetchCurrentNeeded(lastWeatherLocation.zonedDateTime))
            fetchCurrentWeather()
    }

    private suspend fun fetchCurrentWeather() {
        weatherNetworkDataSource.fetchCurrentWeather(
            locationService.getPreferredLocationName()
        )
    }

    private suspend fun fetchHourlyWeather() {
        weatherNetworkDataSource.fetchHourlyWeather(
            locationService.getPreferredLocationName()
        )
    }

    private suspend fun fetchDailyWeather() {
        weatherNetworkDataSource.fetchDailyWeather(
            locationService.getPreferredLocationCoordinates().first,
            locationService.getPreferredLocationCoordinates().second
        )
    }

    private suspend fun fetchPastWeather() {
        weatherNetworkDataSource.fetchPastWeather(
            locationService.getPreferredLocationCoordinates().first,
            locationService.getPreferredLocationCoordinates().second
        )
    }

    private fun isFetchCurrentNeeded(lastFetchTime: ZonedDateTime): Boolean {
        val thirtyMinutesAgo = ZonedDateTime.now().minusMinutes(1)
        return lastFetchTime.isBefore(thirtyMinutesAgo)
    }

    private fun isFetchHourlyNeeded(lastFetchTime: ZonedDateTime): Boolean {
        TODO()
    }

    private fun isFetchDailyNeeded(lastFetchTime: ZonedDateTime): Boolean {
        TODO()
    }
}