package com.example.weatherapp.ui.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapp.data.mappers.CurrentWeatherMapper
import com.example.weatherapp.data.mappers.DailyWeatherMapper
import com.example.weatherapp.data.models.PastWeatherResponse
import com.example.weatherapp.data.networking.ApiConfig
import com.example.weatherapp.domain.models.CurrentWeather
import com.example.weatherapp.domain.models.PastWeather
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CorrelationViewModel : ViewModel() {
    private val openMeteoApiService = ApiConfig.getOpenMeteoApiService()
    private val dailyWeatherMapper = DailyWeatherMapper()
    private val _correlationList = MutableLiveData<List<Int>>()
    val correlationList: LiveData<List<Int>> = _correlationList

    private val _correlationListCity1 = MutableLiveData<List<Int>>()
    val correlationListCity1: LiveData<List<Int>> = _correlationListCity1
    private val _correlationListCity2 = MutableLiveData<List<Int>>()
    val correlationListCity2: LiveData<List<Int>> = _correlationListCity2
    fun fetchCorrelationData(latitude: Double, longitude: Double, city: Int) {
        openMeteoApiService.getPastWeather(latitude, longitude, pastDays=61)
            .enqueue(object : Callback<PastWeatherResponse> {
                override fun onResponse(
                    call: Call<PastWeatherResponse>,
                    response: Response<PastWeatherResponse>
                ) {
                    if (response.isSuccessful) {
                        val pastWeatherResponse: PastWeatherResponse? = response.body()
                        if (pastWeatherResponse != null) {
                            // Convert the API response to the domain model HourlyWeather
                            val correlationList =
                                dailyWeatherMapper.mapCorrelationDataToDomain(pastWeatherResponse)
                            if (city == 1) {
                                _correlationListCity1.value = correlationList
                            } else if (city == 2) {
                                _correlationListCity2.value = correlationList
                            }
                            //val correlationList  = dailyWeatherMapper.mapCorrelationDataToDomain(pastWeatherResponse)
                            //_correlationList.value = correlationList
                            //Log.i("entries", correlationList.toString())
                        }
                    } else {
                        // TODO: Handle API error case
                    }
                }

                override fun onFailure(call: Call<PastWeatherResponse>, t: Throwable) {
                    TODO("Not yet implemented")
                }
            })
    }
}