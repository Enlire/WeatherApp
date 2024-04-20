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
import com.example.weatherapp.data.models.WeatherLocation
import com.example.weatherapp.data.networking.WeatherNetworkDataSource
import com.example.weatherapp.domain.LocationService
import com.example.weatherapp.domain.models.CurrentWeather
import com.example.weatherapp.domain.models.DailyWeather
import com.example.weatherapp.domain.models.HourlyWeather
import com.example.weatherapp.domain.models.PastWeather
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

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
            if (needToFetchCurrent()) fetchCurrentWeather()
            return@withContext currentWeatherDao.getCurrentWeather()
        }
    }

    override suspend fun getHourlyWeatherDataFromDb(): LiveData<out List<HourlyWeather>> {
        return withContext(Dispatchers.IO) {
            if (needToFetchHourly()) fetchHourlyWeather()
            return@withContext hourlyWeatherDao.getHourlyWeather()
        }
    }

    override suspend fun getDailyWeatherDataFromDb(): LiveData<out List<DailyWeather>> {
        return withContext(Dispatchers.IO) {
            if (needToFetchDaily()) fetchDailyWeather()
            return@withContext dailyWeatherDao.getDailyWeather()
        }
    }

    override suspend fun getPastWeatherFromDb(): LiveData<out List<PastWeather>> {
        return withContext(Dispatchers.IO) {
            if (needToFetchPast()) fetchPastWeather()
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
    private fun persistFetchedHourlyWeather(fetchedHourlyWeather: HourlyWeatherResponse) {
        GlobalScope.launch(Dispatchers.IO) {
            hourlyWeatherDao.deleteAllHourlyWeather()
            hourlyWeatherDao.insert(hourlyWeatherMapper.mapHourlyResponseToDomain(
                fetchedHourlyWeather,
                fetchedHourlyWeather.location.localtime)
            )
            weatherLocationDao.upsert(fetchedHourlyWeather.location)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun persistFetchedDailyWeather(fetchedDailyWeather: DailyWeatherResponse) {
        GlobalScope.launch(Dispatchers.IO) {
            dailyWeatherDao.deleteAllDailyWeather()
            dailyWeatherDao.insert(dailyWeatherMapper.mapDailyResponseToDomain(fetchedDailyWeather))
            weatherLocationDao.upsert(dailyWeatherMapper.mapLocationDailyResponse(
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
            weatherLocationDao.upsert(dailyWeatherMapper.mapLocationDailyResponse(fetchedPastWeather.latitude,
                fetchedPastWeather.longitude,
                fetchedPastWeather.utcOffsetSeconds,
                fetchedPastWeather.timezone))
        }
    }

    private suspend fun needToFetchCurrent(): Boolean {
        val lastWeatherLocation = weatherLocationDao.getLocationNonLive()
        return lastWeatherLocation == null
                || locationService.hasLocationChanged(lastWeatherLocation)
                || !(lastWeatherLocation.name.equals(currentWeatherDao.getCurrentWeatherLocation(), ignoreCase = true))
                || isFetchCurrentNeeded(lastWeatherLocation.zonedDateTime)
    }

    private suspend fun needToFetchHourly(): Boolean {
        val lastWeatherLocation = weatherLocationDao.getLocationNonLive()
        return lastWeatherLocation == null
                || locationService.hasLocationChanged(lastWeatherLocation)
                || !(lastWeatherLocation.name.equals(hourlyWeatherDao.getHourlyWeatherLocation(), ignoreCase = true))
                || isFetchHourlyNeeded()
    }

    private suspend fun needToFetchDaily(): Boolean {
        val lastWeatherLocation = weatherLocationDao.getLocationNonLive()
        return lastWeatherLocation == null
                || locationService.hasLocationChanged(lastWeatherLocation)
                || !(lastWeatherLocation.name.equals(dailyWeatherDao.getDailyWeatherLocation(), ignoreCase = true))
                || isFetchDailyNeeded()
    }

    private suspend fun needToFetchPast(): Boolean {
        val lastWeatherLocation = weatherLocationDao.getLocationNonLive()
        return lastWeatherLocation == null
                || locationService.hasLocationChanged(lastWeatherLocation)
                || !(lastWeatherLocation.name.equals(pastWeatherDao.getPastWeatherLocation(), ignoreCase = true))
                || isFetchPastNeeded()
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
        val fifteenMinutesAgo = ZonedDateTime.now(ZoneId.of(weatherLocationDao.getTzId())).minusMinutes(15)
        return lastFetchTime.isBefore(fifteenMinutesAgo)
    }

    private fun isFetchHourlyNeeded(): Boolean {
        val currentTime = ZonedDateTime.now(ZoneId.of(weatherLocationDao.getTzId()))
        val hourlyWeatherCount = hourlyWeatherDao.countHourlyWeather(currentTime)
        return hourlyWeatherCount < 24
    }

    private fun isFetchDailyNeeded(): Boolean {
        val date = ZonedDateTime.now(ZoneId.of(weatherLocationDao.getTzId()))
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formattedDate = date.format(dateFormatter)
        val dailyWeatherCount = dailyWeatherDao.countDailyWeather(formattedDate)
        return dailyWeatherCount < 7
    }

    private fun isFetchPastNeeded(): Boolean {
        val date = ZonedDateTime.now(ZoneId.of(weatherLocationDao.getTzId()))
        val dateFormatter = DateTimeFormatter.ofPattern("dd.MM")
        val formattedDate = date.format(dateFormatter)
        val pastWeatherCount = pastWeatherDao.countPastWeather(formattedDate)
        return pastWeatherCount == 8
    }
}