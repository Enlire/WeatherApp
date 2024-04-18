package com.example.weatherapp.data.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapp.domain.models.PastWeather
import org.threeten.bp.LocalDate

@Dao
interface PastWeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(pastWeatherEntries: List<PastWeather>)

    @Query("SELECT * FROM past_weather")
    fun getPastWeather(): LiveData<List<PastWeather>>

    @Query("SELECT count(id) FROM past_weather WHERE date(date) <= date(:finishDay)")
    fun countDailyWeather(finishDay: LocalDate): Int

    @Query("DELETE FROM past_weather WHERE date(date) > date(:lastDayToKeep)")
    fun deleteOldDailyWeatherEntries(lastDayToKeep: LocalDate)

    @Query("DELETE FROM past_weather")
    fun deleteAllPastWeather()
}