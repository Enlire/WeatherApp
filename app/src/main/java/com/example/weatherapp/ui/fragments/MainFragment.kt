package com.example.weatherapp.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.data.networking.NetworkUtils
import com.example.weatherapp.domain.LocationService
import com.example.weatherapp.domain.models.CurrentWeather
import com.example.weatherapp.domain.models.PastWeather
import com.example.weatherapp.domain.models.WeatherCondition
import com.example.weatherapp.ui.adapters.DailyCardsAdapter
import com.example.weatherapp.ui.adapters.HourlyCardsAdapter
import com.example.weatherapp.ui.dialogs.DialogUtils
import com.example.weatherapp.ui.viewModels.DailyWeatherViewModel
import com.example.weatherapp.ui.viewModels.HourlyWeatherViewModel
import com.example.weatherapp.ui.viewModels.MainViewModel
import com.facebook.shimmer.ShimmerFrameLayout
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter


class MainFragment : Fragment() {

    private lateinit var shimmerLayout: ShimmerFrameLayout
    private lateinit var constraintLayout: ConstraintLayout
    private lateinit var lineChart: LineChart
    private lateinit var chartCardView: CardView

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

        //sharedPreferences.edit().putBoolean("USE_DEVICE_LOCATION", false).apply()

        val locationService = LocationService(requireContext())
        val locationData: Triple<Double, Double, String> = locationService.getLocation()
        val (latitude, longitude, locationName) = locationData
        observationCount = 0

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        viewModelHourly = ViewModelProvider(this)[HourlyWeatherViewModel::class.java]
        viewModelDaily = ViewModelProvider(this)[DailyWeatherViewModel::class.java]

        shimmerLayout = view.findViewById(R.id.shimmer_view_container)
        constraintLayout = view.findViewById(R.id.constraint_layout)
        shimmerLayout.visibility = View.VISIBLE
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

        // Setting up charts
        chartCardView = view.findViewById(R.id.chartCardView)
        lineChart = LineChart(requireContext())
        chartCardView.addView(lineChart)
        viewModel.fetchPastWeatherData(latitude, longitude)
        viewModel.pastWeatherList.observe(viewLifecycleOwner) { pastWeatherList ->
            updateLineChart(pastWeatherList)
        }
    }

    private fun updateLineChart(pastWeatherList: List<PastWeather>) {
        val entriesTempMin = ArrayList<Entry>()
        val entriesTempMax = ArrayList<Entry>()
        val xValues = ArrayList<String>()

        // Transformation pastWeatherList into Entry
        for ((index, pastWeather) in pastWeatherList.take(7).withIndex()) {
            val tempMin = pastWeather.tempMin.toFloat()
            val tempMax = pastWeather.tempMax.toFloat()
            entriesTempMin.add(Entry(index.toFloat(), tempMin))
            entriesTempMax.add(Entry(index.toFloat(), tempMax))
        }

        for ((index, pastWeather) in pastWeatherList.take(7).withIndex()) {
            xValues.add(pastWeather.date)
        }

        val dataSetTempMin = LineDataSet(entriesTempMin, "Мин. температура")
        val dataSetTempMax = LineDataSet(entriesTempMax, "Макс. температура")

        // Charts appearance
        // Min temp chart
        dataSetTempMin.color = ContextCompat.getColor(requireContext(), R.color.min_chart)
        dataSetTempMin.setCircleColor(ContextCompat.getColor(requireContext(), R.color.min_chart))
        dataSetTempMin.circleHoleColor = ContextCompat.getColor(requireContext(), R.color.min_chart)
        dataSetTempMin.lineWidth = 2f
        dataSetTempMin.setDrawValues(false)
        // Max temp chart
        dataSetTempMax.color = ContextCompat.getColor(requireContext(), R.color.max_chart)
        dataSetTempMax.setCircleColor(ContextCompat.getColor(requireContext(), R.color.max_chart))
        dataSetTempMax.circleHoleColor = ContextCompat.getColor(requireContext(), R.color.max_chart)
        dataSetTempMax.lineWidth = 2f
        dataSetTempMax.setDrawValues(false)

        val lineData = LineData(dataSetTempMin, dataSetTempMax)
        lineChart.data = lineData
        lineChart.description.isEnabled = false

        // Axis settings
        lineChart.axisLeft.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                return value.toInt().toString() + "°C"
            }
        }
        lineChart.axisRight.setDrawGridLines(false)
        lineChart.axisLeft.textColor = ContextCompat.getColor(requireContext(), R.color.main_text)
        lineChart.axisLeft.setDrawGridLines(false)
        lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(xValues)
        lineChart.xAxis.textColor = ContextCompat.getColor(requireContext(), R.color.main_text)
        lineChart.axisRight.isEnabled = false
        lineChart.setTouchEnabled(false)

        // Padding settings
        val paddingDp = 6
        val density = resources.displayMetrics.density
        val padding = (paddingDp * density)
        val paddingLeft = (3 * density)
        lineChart.setExtraOffsets(paddingLeft, padding, padding, padding)

        // Legend settings
        val legend = lineChart.legend
        legend.form = Legend.LegendForm.LINE
        legend.textSize = 12f
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.textColor = ContextCompat.getColor(requireContext(), R.color.main_text)
        legend.yOffset = 8f
        legend.xOffset = -11f

        lineChart.invalidate()
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
            val fragmentId = R.id.home
            DialogUtils.showLocationEnableDialog(childFragmentManager, fragmentId)
        } else {
            // Fetch weather data and update UI based on location settings
            viewModelDaily.fetchDailyWeather(latitude, longitude)
            viewModel.fetchCurrentWeatherData(locationName)
            viewModelHourly.fetchHourlyWeather(locationName, latitude, longitude)

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

    @SuppressLint("SetTextI18n")
    private fun showCurrentWeather(currentWeather: CurrentWeather) {
        val windDirection = weatherDescription.translateWindDir(currentWeather.windDir)
        location.text = currentWeather.location
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

    private fun onObservationComplete() {
        observationCount++

        if (observationCount == 3) {
            shimmerLayout.stopShimmer()
            shimmerLayout.visibility = View.GONE
            constraintLayout.visibility = View.VISIBLE
        }
    }
}