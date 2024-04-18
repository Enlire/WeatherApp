package com.example.weatherapp.data.repository

import androidx.lifecycle.LiveData
import com.example.weatherapp.data.daos.CurrentWeatherDao
import com.example.weatherapp.data.daos.DailyWeatherDao
import com.example.weatherapp.data.daos.HourlyWeatherDao
import com.example.weatherapp.data.daos.PastWeatherDao
import com.example.weatherapp.data.daos.WeatherLocationDao
import com.example.weatherapp.data.mappers.CurrentWeatherMapper
import com.example.weatherapp.data.mappers.DailyWeatherMapper
import com.example.weatherapp.data.mappers.HourlyWeatherMapper
import com.example.weatherapp.data.models.CurrentWeatherResponse
import com.example.weatherapp.data.models.DailyWeatherResponse
import com.example.weatherapp.data.models.HourlyWeatherResponse
import com.example.weatherapp.data.models.PastWeatherResponse
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
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import org.threeten.bp.ZonedDateTime

class WeatherRepositoryImpl(
    private val currentWeatherDao: CurrentWeatherDao,
    private val hourlyWeatherDao: HourlyWeatherDao,
    private val dailyWeatherDao: DailyWeatherDao,
    private val pastWeatherDao: PastWeatherDao,
    private val weatherLocationDao: WeatherLocationDao,
    private val weatherNetworkDataSource: WeatherNetworkDataSource,
    private val locationService: LocationService,
    private val currentWeatherMapper: CurrentWeatherMapper,
    private val hourlyWeatherMapper: HourlyWeatherMapper,
    private val dailyWeatherMapper: DailyWeatherMapper

) : WeatherRepository {
    init {
        weatherNetworkDataSource.apply {
            downloadedCurrentWeather.observeForever { newCurrentWeather ->
                persistFetchedCurrentWeather(newCurrentWeather)
            }
            downloadedHourlyWeather.observeForever {newHourlyWeather ->
                persistFetchedHourlyWeather(newHourlyWeather)
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

    override suspend fun getHourlyWeatherDataFromDb(startHour: LocalDateTime): LiveData<out List<HourlyWeather>> {
        return withContext(Dispatchers.IO) {
            initWeatherData()
            return@withContext hourlyWeatherDao.getHourlyWeather(startHour)
        }
    }

    override suspend fun getDailyWeatherDataFromDb(startDate: LocalDate): LiveData<out List<DailyWeather>> {
        return withContext(Dispatchers.IO) {
            initWeatherData()
            return@withContext dailyWeatherDao.getDailyWeather(startDate)
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
    private fun persistFetchedCurrentWeather(fetchedCurrentWeather: CurrentWeatherResponse) {
        GlobalScope.launch(Dispatchers.IO) {
            currentWeatherDao.upsert(currentWeatherMapper.mapCurrentResponseToDomain(fetchedCurrentWeather))
            weatherLocationDao.upsert(fetchedCurrentWeather.location)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun persistFetchedLocation(fetchedLocation: WeatherLocation) {
        GlobalScope.launch(Dispatchers.IO) {
            weatherLocationDao.upsert(fetchedLocation)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun persistFetchedHourlyWeather(fetchedHourlyWeather: HourlyWeatherResponse) {
        fun deleteOldHourlyWeatherData() {
            val currentTime = LocalDateTime.now()
            hourlyWeatherDao.deleteOldHourlyWeatherEntries(currentTime)
        }

        GlobalScope.launch(Dispatchers.IO) {
            deleteOldHourlyWeatherData()
            hourlyWeatherDao.insert(hourlyWeatherMapper.mapHourlyResponseToDomain(
                fetchedHourlyWeather,
                fetchedHourlyWeather.location.localtime)
            )
            weatherLocationDao.upsert(fetchedHourlyWeather.location)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun persistFetchedDailyWeather(fetchedDailyWeather: DailyWeatherResponse) {
        fun deleteOldDailyWeatherData() {
            val today = LocalDate.now()
            dailyWeatherDao.deleteOldDailyWeatherEntries(today)
        }

        GlobalScope.launch(Dispatchers.IO) {
            deleteOldDailyWeatherData()
            dailyWeatherDao.insert(dailyWeatherMapper.mapDailyResponseToDomain(fetchedDailyWeather))
            weatherLocationDao.upsert(dailyWeatherMapper.mapLocationResponse(
                fetchedDailyWeather.latitude,
                fetchedDailyWeather.longitude,
                fetchedDailyWeather.utcOffsetSeconds,
                fetchedDailyWeather.timezone)
            )
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun persistFetchedPastWeather(fetchedPastWeather: PastWeatherResponse) {
        GlobalScope.launch(Dispatchers.IO) {
            pastWeatherDao.deleteAllPastWeather()
            pastWeatherDao.upsert(dailyWeatherMapper.mapPastResponseToDomain(fetchedPastWeather))
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

//        if (isFetchHourlyNeeded())
//            fetchHourlyWeather()
//
//        if (isFetchDailyNeeded())
//            fetchDailyWeather()
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
        val thirtyMinutesAgo = ZonedDateTime.now().minusMinutes(15)
        return lastFetchTime.isBefore(thirtyMinutesAgo)
    }

    private fun isFetchHourlyNeeded(): Boolean {
        val currentTime = LocalDateTime.now()
        val hourlyWeatherCount = hourlyWeatherDao.countHourlyWeather(currentTime)
        return hourlyWeatherCount < 24
    }

    private fun isFetchDailyNeeded(): Boolean {
        val today = LocalDate.now()
        val dailyWeatherCount = dailyWeatherDao.countDailyWeather(today)
        return dailyWeatherCount < 7
    }

    private fun isFetchPastNeeded(): Boolean {
        val today = LocalDate.now()
        val dailyWeatherCount = dailyWeatherDao.countDailyWeather(today)
        return dailyWeatherCount < 7
    }
}