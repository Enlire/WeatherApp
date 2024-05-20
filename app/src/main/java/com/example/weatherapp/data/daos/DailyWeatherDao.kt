package com.example.weatherapp.data.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapp.domain.models.DailyWeather

@Dao
interface DailyWeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(dailyWeather: List<DailyWeather>)

    @Query("SELECT location FROM daily_weather WHERE id = 1")
    fun getDailyWeatherLocation(): String

    @Query("SELECT * FROM daily_weather")
    fun getDailyWeather(): LiveData<List<DailyWeather>>

    @Query("SELECT count(id) FROM daily_weather WHERE date >= :startDay")
    fun countDailyWeather(startDay: String): Int

    @Query("DELETE FROM daily_weather")
    fun deleteAllDailyWeather()
}