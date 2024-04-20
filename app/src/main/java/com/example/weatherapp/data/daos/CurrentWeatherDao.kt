package com.example.weatherapp.data.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapp.domain.models.CURRENT_WEATHER_ID
import com.example.weatherapp.domain.models.CurrentWeather

@Dao
interface CurrentWeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(currentWeather: CurrentWeather)

    @Query("SELECT * FROM current_weather WHERE id = $CURRENT_WEATHER_ID")
    fun getCurrentWeather(): LiveData<CurrentWeather>

    @Query("SELECT location FROM current_weather WHERE id = $CURRENT_WEATHER_ID")
    fun getCurrentWeatherLocation(): String
}