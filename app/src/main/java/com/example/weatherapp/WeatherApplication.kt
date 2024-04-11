package com.example.weatherapp

import android.app.Application
import androidx.preference.PreferenceManager
import com.example.weatherapp.data.ForecastDatabase
import com.example.weatherapp.data.mappers.CurrentWeatherMapper
import com.example.weatherapp.data.networking.OpenMeteoApiService
import com.example.weatherapp.data.networking.WeatherApiService
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.data.repository.WeatherRepositoryImpl
import com.example.weatherapp.ui.viewModels.MainViewModelFactory
import com.jakewharton.threetenabp.AndroidThreeTen
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.*
import org.kodein.di.Kodein.Module
import org.threeten.bp.LocalDate

class WeatherApp : Application(), KodeinAware {
    override val kodein = Kodein.lazy {
        import(androidXModule(this@WeatherApp))

        bind() from singleton { ForecastDatabase(instance()) }
        bind() from singleton { instance<ForecastDatabase>().currentWeatherDao() }
        bind<WeatherApiService>() with singleton { WeatherApiService() }
        bind<OpenMeteoApiService>() with singleton { OpenMeteoApiService() }
        bind<WeatherRepository>() with singleton { WeatherRepositoryImpl(instance(), instance(), instance(), instance()) }
        bind() from provider { MainViewModelFactory(instance()) }
        bind<CurrentWeatherMapper>() with singleton { CurrentWeatherMapper() }
    }

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)
    }
}