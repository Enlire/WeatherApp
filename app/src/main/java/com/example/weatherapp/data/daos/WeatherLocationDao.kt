package com.example.weatherapp.data.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapp.data.models.WEATHER_LOCATION_ID
import com.example.weatherapp.data.models.WeatherLocation

@Dao
interface WeatherLocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(weatherLocation: WeatherLocation)

    @Query("SELECT * FROM weather_location WHERE id = $WEATHER_LOCATION_ID")
    fun getLocation(): LiveData<WeatherLocation>

    @Query("SELECT * FROM weather_location WHERE id = $WEATHER_LOCATION_ID")
    fun getLocationNonLive(): WeatherLocation?

    @Query("SELECT name FROM weather_location WHERE id = $WEATHER_LOCATION_ID")
    fun getLocationName(): String

    @Query("SELECT localTimeEpoch FROM weather_location WHERE id = $WEATHER_LOCATION_ID")
    fun getLocalTimeEpoch(): Long

    @Query("SELECT tzId FROM weather_location WHERE id = $WEATHER_LOCATION_ID")
    fun getTzId(): String
}