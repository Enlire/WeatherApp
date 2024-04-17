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
    fun upsert(dailyWeatherEntries: List<DailyWeather>)

    @Query("SELECT * FROM daily_weather")
    fun getDailyWeather(): LiveData<List<DailyWeather>>

    @Query("DELETE FROM daily_weather")
    fun deleteAllDailyWeather()
}