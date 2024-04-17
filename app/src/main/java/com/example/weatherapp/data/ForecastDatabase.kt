package com.example.weatherapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weatherapp.data.daos.CurrentWeatherDao
import com.example.weatherapp.data.daos.DailyWeatherDao
import com.example.weatherapp.data.daos.HourlyWeatherDao
import com.example.weatherapp.data.daos.PastWeatherDao
import com.example.weatherapp.data.daos.WeatherLocationDao
import com.example.weatherapp.domain.models.CurrentWeather
import com.example.weatherapp.data.models.WeatherLocation
import com.example.weatherapp.domain.models.DailyWeather
import com.example.weatherapp.domain.models.HourlyWeather
import com.example.weatherapp.domain.models.PastWeather

@Database(
    entities = [CurrentWeather::class,
                WeatherLocation::class,
                HourlyWeather::class,
                DailyWeather::class,
                PastWeather::class],
    version = 1
)
abstract class ForecastDatabase: RoomDatabase() {
    abstract fun currentWeatherDao(): CurrentWeatherDao
    abstract fun weatherLocationDao(): WeatherLocationDao
    abstract fun hourlyWeatherDao(): HourlyWeatherDao
    abstract fun dailyWeatherDao(): DailyWeatherDao
    abstract fun pastWeatherDao(): PastWeatherDao

    companion object {
        @Volatile private var instance: ForecastDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance?: synchronized(LOCK) {
            instance?: buildDatabase(context).also { instance = it }
        }
        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext,
                ForecastDatabase::class.java, "forecast.db")
                .build()
    }
}