package com.example.weatherapp.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.data.networking.WeatherNetworkDataSource
import com.example.weatherapp.domain.models.CurrentWeather
import com.example.weatherapp.domain.models.WeatherCondition
import com.example.weatherapp.ui.ErrorCallback
import com.example.weatherapp.ui.adapters.HourlyCardsAdapter
import com.example.weatherapp.ui.dialogs.DialogUtils
import com.example.weatherapp.ui.viewModels.DailyWeatherViewModel
import com.example.weatherapp.ui.viewModels.HourlyWeatherViewModel
import com.example.weatherapp.ui.viewModels.HourlyWeatherViewModelFactory
import com.example.weatherapp.ui.viewModels.MainViewModel
import com.example.weatherapp.ui.viewModels.MainViewModelFactory
import com.facebook.shimmer.ShimmerFrameLayout
import com.github.mikephil.charting.charts.LineChart
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class MainFragment : ScopedFragment(), KodeinAware {
    override val kodein by closestKodein()
    private val viewModelFactory: MainViewModelFactory by instance()
    private val hourlyWeatherViewModelFactory: HourlyWeatherViewModelFactory by instance()
    private val weatherNetworkDataSource: WeatherNetworkDataSource by instance()

    // Layouts and views
    private lateinit var shimmerLayout: ShimmerFrameLayout
    private lateinit var constraintLayout: ConstraintLayout
    private lateinit var lineChart: LineChart
    private lateinit var chartCardView: CardView

    // ViewModels
    private lateinit var viewModel: MainViewModel
    private lateinit var viewModelHourly: HourlyWeatherViewModel
    private lateinit var viewModelDaily: DailyWeatherViewModel

    // Current weather data
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        shimmerLayout = view.findViewById(R.id.shimmer_view_container)
        constraintLayout = view.findViewById(R.id.constraint_layout)

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
        viewModelHourly = ViewModelProvider(this, hourlyWeatherViewModelFactory)[HourlyWeatherViewModel::class.java]

        val recyclerViewHourly: RecyclerView = view.findViewById(R.id.hourlyWeatherRecyclerView)
        val hourlyAdapter = HourlyCardsAdapter(emptyList())
        recyclerViewHourly.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewHourly.adapter = hourlyAdapter


        shimmerLayout = view.findViewById(R.id.shimmer_view_container)
        constraintLayout = view.findViewById(R.id.constraint_layout)
        shimmerLayout.visibility = View.VISIBLE
        shimmerLayout.startShimmer()

        observeWeatherDataChanges(hourlyAdapter)
    }

    private fun observeWeatherDataChanges(hourlyAdapter: HourlyCardsAdapter) = launch {
        val currentWeather = viewModel.currentWeather.await()
        val hourlyWeather = viewModelHourly.hourlyWeather.await()
        val weatherLocation = viewModel.weatherLocation.await()

        weatherLocation.observe(viewLifecycleOwner, Observer { location ->
            if (location == null) return@Observer
        })

        currentWeather.observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer
            showCurrentWeather(it)
        })

        hourlyWeather.observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer
            hourlyAdapter.updateData(it)
        })

        viewModel.fetchCurrentWeatherData()
        viewModelHourly.fetchHourlyWeatherData()

        shimmerLayout.stopShimmer()
        shimmerLayout.visibility = View.GONE
        constraintLayout.visibility = View.VISIBLE

        // Observe errors
        weatherNetworkDataSource.setErrorCallback(object : ErrorCallback {
            override fun onError(errorMessage: String?) {
                if (!errorMessage.isNullOrBlank()) {
                    requireActivity().runOnUiThread {
                        if (isAdded) {
                            DialogUtils.showAPIErrorDialog(childFragmentManager, errorMessage)
                            shimmerLayout.visibility = View.VISIBLE
                            shimmerLayout.startShimmer()
                            constraintLayout.visibility = View.GONE
                        }
                    }
                }
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun showCurrentWeather(currentWeather: CurrentWeather) {
        val windDirection = WeatherCondition().translateWindDir(currentWeather.windDir)
        location.text = currentWeather.location.name
        temp.text = "${currentWeather.temperature}°C"
        weatherCondition.text = currentWeather.description
        weatherIcon.setImageResource(currentWeather.icResId)
        weatherIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.main_text))
        feelsLike.text = "${currentWeather.feelsLike}°C"
        windDir.text = windDirection
        windSpeed.text = "${currentWeather.windSpeed} м/с"
        humidity.text = "${currentWeather.humidity}%"
        visibility.text = "${currentWeather.visibility} км"
        uvIndex.text = currentWeather.uvIndex.toString()
        pressure.text = "${currentWeather.pressure} мм рт. ст."
    }
}