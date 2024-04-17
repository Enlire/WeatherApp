package com.example.weatherapp.data.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapp.domain.models.HourlyWeather
import org.threeten.bp.LocalDate

@Dao
interface HourlyWeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(hourlyWeatherEntries: List<HourlyWeather>)

    @Query("SELECT * FROM hourly_weather")
    fun getHourlyWeather(): LiveData<List<HourlyWeather>>

    @Query("DELETE FROM hourly_weather")
    fun deleteAllHourlyWeather()
}