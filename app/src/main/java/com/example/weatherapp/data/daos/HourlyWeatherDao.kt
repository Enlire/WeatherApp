package com.example.weatherapp.data.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapp.domain.models.HourlyWeather
import org.threeten.bp.ZonedDateTime

@Dao
interface HourlyWeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(hourlyWeather: List<HourlyWeather>)

    @Query("SELECT location FROM hourly_weather WHERE id = 1")
    fun getHourlyWeatherLocation(): String

    @Query("SELECT * FROM hourly_weather")
    fun getHourlyWeather(): LiveData<List<HourlyWeather>>

    @Query("SELECT count(id) FROM hourly_weather WHERE dateTime(date) >= dateTime(:startDateTime)")
    fun countHourlyWeather(startDateTime: ZonedDateTime): Int

    @Query("DELETE FROM hourly_weather")
    fun deleteAllHourlyWeather()
}