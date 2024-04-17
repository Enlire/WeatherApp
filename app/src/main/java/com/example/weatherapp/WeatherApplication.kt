package com.example.weatherapp

import android.app.Application
import android.content.Context
import androidx.preference.PreferenceManager
import com.example.weatherapp.data.ForecastDatabase
import com.example.weatherapp.data.mappers.CurrentWeatherMapper
import com.example.weatherapp.data.mappers.DailyWeatherMapper
import com.example.weatherapp.data.mappers.HourlyWeatherMapper
import com.example.weatherapp.data.networking.OpenMeteoApiService
import com.example.weatherapp.data.networking.WeatherApiService
import com.example.weatherapp.data.networking.WeatherNetworkDataSource
import com.example.weatherapp.data.networking.WeatherNetworkDataSourceImpl
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.data.repository.WeatherRepositoryImpl
import com.example.weatherapp.domain.LocationService
import com.example.weatherapp.domain.LocationServiceImpl
import com.example.weatherapp.ui.viewModelsFactories.DailyWeatherViewModelFactory
import com.example.weatherapp.ui.viewModelsFactories.HourlyWeatherViewModelFactory
import com.example.weatherapp.ui.viewModelsFactories.MainViewModelFactory
import com.google.android.gms.location.LocationServices
import com.jakewharton.threetenabp.AndroidThreeTen
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.*

class WeatherApp : Application(), KodeinAware {
    override val kodein = Kodein.lazy {
        import(androidXModule(this@WeatherApp))

        bind() from singleton { ForecastDatabase(instance()) }
        bind() from singleton { instance<ForecastDatabase>().currentWeatherDao() }
        bind() from singleton { instance<ForecastDatabase>().hourlyWeatherDao() }
        bind() from singleton { instance<ForecastDatabase>().dailyWeatherDao() }
        bind() from singleton { instance<ForecastDatabase>().pastWeatherDao() }
        bind() from singleton { instance<ForecastDatabase>().weatherLocationDao() }
        bind() from provider { LocationServices.getFusedLocationProviderClient(instance<Context>()) }
        bind<WeatherApiService>() with singleton { WeatherApiService() }
        bind<OpenMeteoApiService>() with singleton { OpenMeteoApiService() }
        bind<WeatherRepository>() with singleton { WeatherRepositoryImpl(instance(), instance(), instance(), instance(), instance(), instance(), instance()) }
        bind<WeatherNetworkDataSource>() with singleton { WeatherNetworkDataSourceImpl(instance(), instance(), instance(), instance(), instance()) }
        bind<LocationService>() with singleton { LocationServiceImpl(instance())}
        bind() from provider { MainViewModelFactory(instance()) }
        bind() from provider { HourlyWeatherViewModelFactory(instance()) }
        bind() from provider { DailyWeatherViewModelFactory(instance()) }
        bind<CurrentWeatherMapper>() with singleton { CurrentWeatherMapper() }
        bind<HourlyWeatherMapper>() with singleton { HourlyWeatherMapper() }
        bind<DailyWeatherMapper>() with singleton { DailyWeatherMapper() }
    }

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)
    }
}