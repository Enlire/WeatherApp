package com.example.weatherapp.ui.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.data.networking.NetworkUtils
import com.example.weatherapp.domain.LocationService
import com.example.weatherapp.domain.models.CurrentWeather
import com.example.weatherapp.domain.models.WeatherCondition
import com.example.weatherapp.ui.DialogUtils
import com.example.weatherapp.ui.SettingsFragment
import com.example.weatherapp.ui.adapters.DailyCardsAdapter
import com.example.weatherapp.ui.adapters.HourlyCardsAdapter
import com.example.weatherapp.ui.viewModels.DailyWeatherViewModel
import com.example.weatherapp.ui.viewModels.HourlyWeatherViewModel
import com.example.weatherapp.ui.viewModels.MainViewModel
import com.facebook.shimmer.ShimmerFrameLayout

class MainFragment : Fragment() {

    private lateinit var shimmerLayout: ShimmerFrameLayout
    private lateinit var constraintLayout: ConstraintLayout

    private lateinit var viewModel: MainViewModel
    private lateinit var viewModelHourly: HourlyWeatherViewModel
    private lateinit var viewModelDaily: DailyWeatherViewModel

    private val weatherDescription = WeatherCondition()
    private val sharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(requireContext())
    }
    private lateinit var location: TextView
    private lateinit var weatherIcon: ImageView
    private lateinit var temp: TextView
    private lateinit var weatherCondition: TextView
    private lateinit var feelsLike: TextView
    private lateinit var windDir: TextView
    private lateinit var windSpeed: TextView
    private lateinit var humidity: TextView
    private lateinit var visibility: TextView
    private lateinit var uvIndex: TextView
    private lateinit var pressure: TextView

    private var observationCount = 0

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

        val locationService = LocationService(requireContext())
        val locationData: Triple<Double, Double, String> = locationService.getLocation()
        val (latitude, longitude, locationName) = locationData

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        viewModelHourly = ViewModelProvider(this)[HourlyWeatherViewModel::class.java]
        viewModelDaily = ViewModelProvider(this)[DailyWeatherViewModel::class.java]

        shimmerLayout = view.findViewById(R.id.shimmer_view_container)
        constraintLayout = view.findViewById(R.id.constraint_layout)

        shimmerLayout.visibility = View.VISIBLE;
        shimmerLayout.startShimmer()

        val recyclerViewHourly: RecyclerView = view.findViewById(R.id.hourlyWeatherRecyclerView)
        val hourlyAdapter = HourlyCardsAdapter(emptyList())
        recyclerViewHourly.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewHourly.adapter = hourlyAdapter

        val recyclerViewDaily: RecyclerView = view.findViewById(R.id.dailyWeatherRecyclerView)
        val dailyAdapter = DailyCardsAdapter(emptyList())
        recyclerViewDaily.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerViewDaily.adapter = dailyAdapter

        // Check Internet connection and display dialog if not available
        if (NetworkUtils.isInternetAvailable(requireContext())) {
            checkLocationSettings(locationName, latitude, longitude, hourlyAdapter, dailyAdapter)
        }
        else {
            DialogUtils.showNoInternetDialog(childFragmentManager)
        }
    }

    private fun checkLocationSettings(
        locationName: String,
        latitude: Double,
        longitude: Double,
        hourlyAdapter: HourlyCardsAdapter,
        dailyAdapter: DailyCardsAdapter
    ) {
        val locationService = LocationService(requireContext())
        val isSwitchEnabled = sharedPreferences.getBoolean("USE_DEVICE_LOCATION", false)

        if (isSwitchEnabled && !locationService.isLocationServiceEnabled()) {
            DialogUtils.showLocationEnableDialog(childFragmentManager)
        } else {
            // Fetch weather data and update UI based on location settings
            viewModelDaily.fetchDailyWeather(latitude, longitude)
            viewModel.fetchCurrentWeatherData(locationName)
            viewModelHourly.fetchHourlyWeather(locationName)

            // Observe weather data changes
            viewModel.weatherData.observe(viewLifecycleOwner) { weatherResponse ->
                showCurrentWeather(weatherResponse)
                onObservationComplete()
            }
            viewModelHourly.hourlyWeatherList.observe(viewLifecycleOwner) { hourlyCardsList ->
                hourlyAdapter.updateData(hourlyCardsList)
                onObservationComplete()
            }
            viewModelDaily.dailyWeatherList.observe(viewLifecycleOwner) { dailyCardsList ->
                dailyAdapter.updateData(dailyCardsList)
                onObservationComplete()
            }
        }
    }

    private fun fetchCurrentWeatherData() {

    }

    @SuppressLint("SetTextI18n")
    private fun showCurrentWeather(currentWeather: CurrentWeather) {
        val windDirection = weatherDescription.translateWindDir(currentWeather.wind_dir)
        location.text = currentWeather.location
        temp.text = "${currentWeather.temperature}°C"
        weatherCondition.text = currentWeather.description
        weatherIcon.setImageResource(currentWeather.icResId)
        weatherIcon.setColorFilter(Color.parseColor("#2B2B2B"))
        feelsLike.text = "${currentWeather.feels_like}°C"
        windDir.text = windDirection
        windSpeed.text = "${currentWeather.wind_speed} м/с"
        humidity.text = "${currentWeather.humidity}%"
        visibility.text = "${currentWeather.visibility} км"
        uvIndex.text = currentWeather.uv_index.toString()
        pressure.text = "${currentWeather.pressure} мм рт. ст."
    }

    private fun onObservationComplete() {
        observationCount++

        if (observationCount == 3) {
            shimmerLayout.stopShimmer()
            shimmerLayout.visibility = View.GONE
            constraintLayout.visibility = View.VISIBLE
        }
    }

}