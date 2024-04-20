package com.example.weatherapp.ui.viewModels

import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.domain.lazyDeferred
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: WeatherRepository
) : WeatherLocationViewModel(repository) {

    val currentWeather by lazyDeferred {
        repository.getCurrentWeatherDataFromDb()
    }

    val pastWeather by lazyDeferred {
        repository.getPastWeatherFromDb()
    }

    fun fetchCurrentWeatherData() {
        viewModelScope.launch {
            repository.getCurrentWeatherDataFromDb()
        }
    }

    fun fetchPastWeatherData() {
        viewModelScope.launch {
            repository.getPastWeatherFromDb()
        }
    }
}