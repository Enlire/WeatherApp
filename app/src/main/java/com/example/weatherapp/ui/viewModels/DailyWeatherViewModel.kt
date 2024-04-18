package com.example.weatherapp.ui.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.mappers.DailyWeatherMapper
import com.example.weatherapp.data.models.DailyWeatherResponse
import com.example.weatherapp.data.networking.ApiConfig
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.domain.lazyDeferred
import com.example.weatherapp.domain.models.DailyWeather
import com.example.weatherapp.ui.ErrorCallback
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DailyWeatherViewModel(
    private val repository: WeatherRepository
) : WeatherLocationViewModel(repository) {

    val dailyWeather by lazyDeferred {
        repository.getDailyWeatherDataFromDb(LocalDate.now())
    }

    fun fetchDailyWeatherData() {
        viewModelScope.launch {
            repository.getDailyWeatherDataFromDb(LocalDate.now())
        }
    }
}