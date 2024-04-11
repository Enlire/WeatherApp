package com.example.weatherapp.data.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Upsert
import com.example.weatherapp.data.models.CurrentWeatherResponse
import com.example.weatherapp.domain.models.CURRENT_WEATHER_ID
import com.example.weatherapp.domain.models.CurrentWeather

@Dao
interface CurrentWeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(currentWeather: CurrentWeather)

    @Query("SELECT * FROM current_weather WHERE id = $CURRENT_WEATHER_ID")
    @RewriteQueriesToDropUnusedColumns
    fun getCurrentWeather(): LiveData<CurrentWeather>
}