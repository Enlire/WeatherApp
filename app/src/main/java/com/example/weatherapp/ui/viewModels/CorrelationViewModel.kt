package com.example.weatherapp.ui.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapp.data.mappers.DailyWeatherMapper
import com.example.weatherapp.data.models.PastWeatherResponse
import com.example.weatherapp.data.networking.ApiConfig
import com.example.weatherapp.ui.ErrorCallback
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CorrelationViewModel : ViewModel() {
    private val openMeteoApiService = ApiConfig.getOpenMeteoApiService()
    //private val dailyWeatherMapper = DailyWeatherMapper()
    private var errorCallback: ErrorCallback? = null

    private val _correlationTempListCity1 = MutableLiveData<List<Float>>()
    val correlationListTempCity1: LiveData<List<Float>> = _correlationTempListCity1
    private val _correlationTempListCity2 = MutableLiveData<List<Float>>()
    val correlationListTempCity2: LiveData<List<Float>> = _correlationTempListCity2

    private val _correlationPrecipListCity1 = MutableLiveData<List<Float>>()
    val correlationListPrecipCity1: LiveData<List<Float>> = _correlationPrecipListCity1
    private val _correlationPrecipListCity2 = MutableLiveData<List<Float>>()
    val correlationListPrecipCity2: LiveData<List<Float>> = _correlationPrecipListCity2
//    fun fetchCorrelationData(latitude: Double, longitude: Double, city: Int) {
//        openMeteoApiService.getPastWeather(latitude, longitude, pastDays=92)
//            .enqueue(object : Callback<PastWeatherResponse> {
//                override fun onResponse(
//                    call: Call<PastWeatherResponse>,
//                    response: Response<PastWeatherResponse>
//                ) {
//                    if (response.isSuccessful) {
//                        val pastWeatherResponse: PastWeatherResponse? = response.body()
//                        if (pastWeatherResponse != null) {
//                            val correlationTempList =
//                                dailyWeatherMapper.mapCorrelationTempToDomain(pastWeatherResponse)
//                            val correlationPrecipList =
//                                dailyWeatherMapper.mapCorrelationPrecipToDomain(pastWeatherResponse)
//
//                            if (city == 1) {
//                                _correlationTempListCity1.value = correlationTempList
//                                _correlationPrecipListCity1.value = correlationPrecipList
//                            } else if (city == 2) {
//                                _correlationTempListCity2.value = correlationTempList
//                                _correlationPrecipListCity2.value = correlationPrecipList
//                            }
//                        }
//                    } else {
//                        errorCallback?.onError("Ошибка при получении данных. Попробуйте повторить запрос.")
//                    }
//                }
//
//                override fun onFailure(call: Call<PastWeatherResponse>, t: Throwable) {
//                    errorCallback?.onError("Ошибка при выполнении запроса к серверу. Попробуйте повторить запрос.")
//                }
//            })
//    }
//
//    fun setErrorCallback(callback: ErrorCallback) {
//        errorCallback = callback
//    }
}