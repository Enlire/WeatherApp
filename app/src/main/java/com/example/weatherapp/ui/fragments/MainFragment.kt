package com.example.weatherapp.ui.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.weatherapp.R
import com.example.weatherapp.data.networking.NetworkUtils
import com.example.weatherapp.domain.models.CurrentWeather
import com.example.weatherapp.domain.models.WeatherCondition
import com.example.weatherapp.ui.dialogs.NoInternetDialogFragment
import com.example.weatherapp.ui.viewModels.MainViewModel

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

    private fun showNoInternetDialog() {
        val dialogFragment = NoInternetDialogFragment()
        dialogFragment.show(childFragmentManager, "NoInternetDialog")
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

        // Check Internet connection and display dialog if not available
        if (!NetworkUtils.isInternetAvailable(requireContext())) {
            showNoInternetDialog()
        } else {
            // Observe weather data changes
            viewModel.weatherData.observe(viewLifecycleOwner) { weatherResponse ->
                showCurrentWeather(weatherResponse)
            }
            // Fetch weather data and update UI
            val location = "Ахтубинск"
            viewModel.fetchWeatherData(location)
        }
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