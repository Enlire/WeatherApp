package com.example.weatherapp.ui.viewModels

import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.domain.lazyDeferred
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDateTime

class HourlyWeatherViewModel(
    private val repository: WeatherRepository
) : WeatherLocationViewModel(repository) {

    val hourlyWeather by lazyDeferred {
        repository.getHourlyWeatherDataFromDb(LocalDateTime.now())
    }

    fun fetchHourlyWeatherData() {
        viewModelScope.launch {
            repository.getHourlyWeatherDataFromDb(LocalDateTime.now())
        }
    }
}