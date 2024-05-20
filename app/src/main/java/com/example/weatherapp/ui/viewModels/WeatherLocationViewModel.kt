package com.example.weatherapp.ui.viewModels

import androidx.lifecycle.ViewModel
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.domain.lazyDeferred

abstract class WeatherLocationViewModel(
    private val repository: WeatherRepository
) : ViewModel() {

    val weatherLocation by lazyDeferred {
        repository.getWeatherLocationFromDb()
    }
}