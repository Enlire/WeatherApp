package com.example.weatherapp.ui.viewModelsFactories

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.ui.viewModels.CorrelationViewModel
import com.example.weatherapp.ui.viewModels.DailyWeatherViewModel

class CorrelationViewModelFactory(
    private val context: Context,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CorrelationViewModel(context) as T
    }
}