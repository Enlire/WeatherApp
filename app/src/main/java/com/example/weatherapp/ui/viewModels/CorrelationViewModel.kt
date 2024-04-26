package com.example.weatherapp.ui.viewModels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapp.data.mappers.DailyWeatherMapper
import com.example.weatherapp.data.models.PastWeatherResponse
import com.example.weatherapp.data.networking.ApiConfig
import com.example.weatherapp.data.networking.OpenMeteoApiService
import com.example.weatherapp.ui.ErrorCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class CorrelationViewModel(context: Context) : ViewModel() {
    private val openMeteoApiService = OpenMeteoApiService()
    private val dailyWeatherMapper = DailyWeatherMapper(context)
    private var errorCallback: ErrorCallback? = null

    private val _correlationTempListCity1 = MutableLiveData<List<Float>>()
    val correlationListTempCity1: LiveData<List<Float>>
        get() = _correlationTempListCity1

    private val _correlationTempListCity2 = MutableLiveData<List<Float>>()
    val correlationListTempCity2: LiveData<List<Float>>
        get() = _correlationTempListCity2

    private val _correlationPrecipListCity1 = MutableLiveData<List<Float>>()
    val correlationListPrecipCity1: LiveData<List<Float>>
        get() = _correlationPrecipListCity1

    private val _correlationPrecipListCity2 = MutableLiveData<List<Float>>()
    val correlationListPrecipCity2: LiveData<List<Float>>
        get() = _correlationPrecipListCity2

    suspend fun getCorrelationData(lat: Double, lon: Double, city: Int) {
        return withContext(Dispatchers.IO) {
            return@withContext fetchCorrelationData(lat, lon, city)
        }
    }

    private suspend fun fetchCorrelationData(lat: Double, lon: Double, city: Int) {
        try {
            val pastWeatherResponse = openMeteoApiService
                .getPastWeather(lat = lat, lon = lon, pastDays = 92)
                .await()
            val correlationTempList = dailyWeatherMapper.mapCorrelationTempToDomain(pastWeatherResponse)
            val correlationPrecipList = dailyWeatherMapper.mapCorrelationPrecipToDomain(pastWeatherResponse)

            if (city == 1) {
                _correlationTempListCity1.postValue(correlationTempList)
                _correlationPrecipListCity1.postValue(correlationPrecipList)
            } else if (city == 2) {
                _correlationTempListCity2.postValue(correlationTempList)
                _correlationPrecipListCity2.postValue(correlationPrecipList)
            }
        } catch (e: IOException) {
            errorCallback?.onError("Ошибка при получении данных о погоде. Попробуйте повторить запрос.")
        }
    }

    fun setErrorCallback(callback: ErrorCallback) {
        errorCallback = callback
    }
}