package com.example.weatherapp.ui.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.domain.lazyDeferred

abstract class WeatherViewModel(
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    val weatherLocation by lazyDeferred {
        weatherRepository.getWeatherLocationFromDb()
    }
}