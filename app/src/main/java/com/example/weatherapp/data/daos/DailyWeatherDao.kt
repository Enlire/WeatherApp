package com.example.weatherapp.data.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapp.domain.models.DailyWeather
import org.threeten.bp.LocalDate

@Dao
interface DailyWeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(dailyWeatherEntries: List<DailyWeather>)

    @Query("SELECT * FROM daily_weather WHERE date(date) >= date(:startDay)")
    fun getDailyWeather(startDay: LocalDate): LiveData<List<DailyWeather>>

    @Query("SELECT count(id) FROM daily_weather WHERE date(date) >= date(:startDay)")
    fun countDailyWeather(startDay: LocalDate): Int

    @Query("DELETE FROM daily_weather WHERE date(date) < date(:firstDayToKeep)")
    fun deleteOldDailyWeatherEntries(firstDayToKeep: LocalDate)

    @Query("DELETE FROM daily_weather")
    fun deleteAllDailyWeather()
}