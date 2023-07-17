package com.example.weatherapp.ui

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.weatherapp.R
import com.example.weatherapp.domain.models.CurrentWeather
import com.example.weatherapp.domain.models.WeatherCondition
import com.squareup.picasso.Picasso

class MainFragment : Fragment() {

    private val weatherDescription = WeatherCondition()
    private lateinit var viewModel: MainViewModel
    private lateinit var location: TextView
    private lateinit var weatherIcon: ImageView
    private lateinit var temp: TextView
    private lateinit var weatherCondition: TextView
    private lateinit var minMaxTemp: TextView
    private lateinit var feelsLike: TextView
    private lateinit var windDir: TextView
    private lateinit var windSpeed: TextView
    private lateinit var humidity: TextView
    private lateinit var visibility: TextView
    private lateinit var uvIndex: TextView
    private lateinit var pressure: TextView

    companion object {
        fun newInstance() = MainFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        location = view.findViewById(R.id.location)
        weatherIcon = view.findViewById(R.id.weatherIcon)
        temp = view.findViewById(R.id.tempC)
        weatherCondition = view.findViewById(R.id.weatherCondition)
        minMaxTemp = view.findViewById(R.id.minMaxTemp)
        feelsLike = view.findViewById(R.id.feels_like)
        windDir = view.findViewById(R.id.wind_dir)
        windSpeed = view.findViewById(R.id.wind_speed)
        humidity = view.findViewById(R.id.humidity)
        visibility = view.findViewById(R.id.visibility)
        uvIndex = view.findViewById(R.id.uv_index)
        pressure = view.findViewById(R.id.pressure)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        // Observe weather data changes
        viewModel.weatherData.observe(viewLifecycleOwner) { weatherResponse ->
            showCurrentWeather(weatherResponse)
        }

        // Fetch weather data
        val location = "Лондон"
        viewModel.fetchWeatherData(location)
    }

    private fun showCurrentWeather(currentWeather: CurrentWeather) {
        val windDirection = weatherDescription.translateWindDir(currentWeather.wind_dir)

        location.text = currentWeather.location
        temp.text = "${currentWeather.temperature}°C"
        weatherCondition.text = currentWeather.description
        feelsLike.text = "${currentWeather.feels_like}°C"
        windDir.text = windDirection
        windSpeed.text = "${currentWeather.wind_speed} м/с"
        humidity.text = "${currentWeather.humidity}%"
        visibility.text = "${currentWeather.visibility} км"
        uvIndex.text = currentWeather.uv_index.toString()
        pressure.text = "${currentWeather.pressure} мм рт. ст."
    }
}

    //    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        viewModel = MainViewModel()
//        subscribe()
//
//        etCityName = view.findViewById(R.id.et_city_name)
//        tvResult = view.findViewById(R.id.tv_result)
//        btnSend = view.findViewById(R.id.btn_send_request)
//
//        // Add on click button to the send button
//        btnSend.setOnClickListener {
//            // Text field validation
//            if (etCityName.text.isNullOrEmpty() or etCityName.text.isNullOrBlank()) {
//                etCityName.error = "Field can't be null"
//            } else {
//                // Get weather data
//                viewModel.getCurrentWeatherData(etCityName.text.toString())
//
//                viewModel.currentWeatherData.observe(viewLifecycleOwner) { currentWeatherData ->
//                    // Display weather data to the UI
//                    setResultText(currentWeatherData)
//                }
//            }
//        }
//    }
//
//    private fun subscribe() {
//        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
//            // Set the result text to Loading
//            if (isLoading) tvResult.text = resources.getString(R.string.loading)
//        }
//
//        viewModel.isError.observe(viewLifecycleOwner) { isError ->
//            // Hide display image and set the result text to the error message
//            if (isError) {
//                tvResult.text = viewModel.errorMessage
//            }
//        }
//
//        viewModel.currentWeatherData.observe(viewLifecycleOwner) { currentWeatherData ->
//            // Display weather data to the UI
//            setResultText(currentWeatherData)
//        }
//    }
//
//    private fun setResultText(currentWeatherData: CurrentWeatherResponse) {
//        val resultText = StringBuilder("Result:\n")
//
//        currentWeatherData.location.let { location ->
//            resultText.append("Name: ${location?.name}\n")
//        }
//
//        currentWeatherData.current.let { current ->
//            current?.condition.let { condition ->
//                resultText.append("Condition: ${condition?.text}\n")
//            }
//            resultText.append("Celcius: ${current?.tempC}\n")
//        }
//
//        tvResult.text = resultText
//    }