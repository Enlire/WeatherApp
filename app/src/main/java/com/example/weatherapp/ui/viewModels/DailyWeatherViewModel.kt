package com.example.weatherapp.ui.viewModels

import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.domain.lazyDeferred
import kotlinx.coroutines.launch

class DailyWeatherViewModel(
    private val repository: WeatherRepository
) : WeatherLocationViewModel(repository) {

    val dailyWeather by lazyDeferred {
        repository.getDailyWeatherDataFromDb()
    }

    fun fetchDailyWeatherData() {
        viewModelScope.launch {
            repository.getDailyWeatherDataFromDb()
        }
    }
}