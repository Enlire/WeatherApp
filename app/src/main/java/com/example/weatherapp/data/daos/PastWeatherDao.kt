package com.example.weatherapp.data.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapp.domain.models.PastWeather

@Dao
interface PastWeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(pastWeatherEntries: List<PastWeather>)

    @Query("SELECT location FROM past_weather WHERE id = 1")
    fun getPastWeatherLocation(): String

    @Query("SELECT * FROM past_weather")
    fun getPastWeather(): LiveData<List<PastWeather>>

    @Query("SELECT count(id) FROM past_weather WHERE date < :startDay")
    fun countPastWeather(startDay: String): Int

    @Query("DELETE FROM past_weather")
    fun deleteAllPastWeather()
}