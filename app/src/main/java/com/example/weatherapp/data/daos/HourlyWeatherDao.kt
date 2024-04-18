package com.example.weatherapp.data.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapp.domain.models.DailyWeather
import com.example.weatherapp.domain.models.HourlyWeather
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime

@Dao
interface HourlyWeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(hourlyWeatherEntries: List<HourlyWeather>)

    @Query("SELECT * FROM hourly_weather WHERE dateTime(date) >= dateTime(:startDateTime)")
    fun getHourlyWeather(startDateTime: LocalDateTime): LiveData<List<HourlyWeather>>

    @Query("SELECT count(id) FROM hourly_weather WHERE dateTime(date) >= date(:startDateTime)")
    fun countHourlyWeather(startDateTime: LocalDateTime): Int

    @Query("DELETE FROM hourly_weather WHERE dateTime(date) < dateTime(:firstDateTimeToKeep)")
    fun deleteOldHourlyWeatherEntries(firstDateTimeToKeep: LocalDateTime)

    @Query("DELETE FROM hourly_weather")
    fun deleteAllHourlyWeather()
}