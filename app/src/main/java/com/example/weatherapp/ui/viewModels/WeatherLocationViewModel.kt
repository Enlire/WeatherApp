package com.example.weatherapp.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.domain.lazyDeferred
import kotlinx.coroutines.launch

abstract class WeatherLocationViewModel(
    private val repository: WeatherRepository
) : ViewModel() {

    val weatherLocation by lazyDeferred {
        repository.getWeatherLocationFromDb()
    }

    fun fetchLocationData() {
        viewModelScope.launch {
            repository.getWeatherLocationFromDb()
        }
    }
}