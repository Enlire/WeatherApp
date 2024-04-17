package com.example.weatherapp.ui.viewModelsFactories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.ui.viewModels.HourlyWeatherViewModel

class HourlyWeatherViewModelFactory(
    private val repository: WeatherRepository,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HourlyWeatherViewModel(repository) as T
    }
}