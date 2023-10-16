package com.example.weatherapp.ui.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.data.networking.NetworkUtils
import com.example.weatherapp.domain.LocationService
import com.example.weatherapp.ui.dialogs.DialogUtils
import com.example.weatherapp.ui.adapters.DailyWeatherAdapter
import com.example.weatherapp.ui.adapters.HourlyWeatherAdapter
import com.example.weatherapp.ui.viewModels.DailyWeatherViewModel
import com.facebook.shimmer.ShimmerFrameLayout

class DailyWeatherFragment : Fragment() {

    companion object {
        fun newInstance() = DailyWeatherFragment()
    }

    private lateinit var viewModel: DailyWeatherViewModel
    private lateinit var shimmerLayout: ShimmerFrameLayout
    private lateinit var recyclerView: RecyclerView
    private val sharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_daily_weather, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val locationService = LocationService(requireContext())
        val locationData: Triple<Double, Double, String> = locationService.getLocation()
        val (latitude, longitude, locationName) = locationData

        recyclerView = view.findViewById(R.id.dailyWeatherRecyclerView)
        val dailyAdapter = DailyWeatherAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = dailyAdapter
        viewModel = ViewModelProvider(this)[DailyWeatherViewModel::class.java]

        shimmerLayout = view.findViewById(R.id.shimmer_view_container)
        shimmerLayout.visibility = View.VISIBLE;
        shimmerLayout.startShimmer()

        // Check Internet connection and display dialog if not available
        if (NetworkUtils.isInternetAvailable(requireContext())) {
            checkLocationSettings(locationName, latitude, longitude, dailyAdapter)
        }
        else {
            DialogUtils.showNoInternetDialog(childFragmentManager)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(DailyWeatherViewModel::class.java)
        // TODO: Use the ViewModel
    }

    private fun checkLocationSettings(
        locationName: String,
        latitude: Double,
        longitude: Double,
        dailyAdapter: DailyWeatherAdapter
    ) {
        val locationService = LocationService(requireContext())
        val isSwitchEnabled = sharedPreferences.getBoolean("USE_DEVICE_LOCATION", false)

        if (isSwitchEnabled && !locationService.isLocationServiceEnabled()) {
            val fragmentId = R.id.daily
            DialogUtils.showLocationEnableDialog(childFragmentManager, fragmentId)
        } else {
            // Fetch the hourly weather data for the desired location
            viewModel.fetchDailyWeather(latitude, longitude)

            // Observe the hourly weather data from the ViewModel
            viewModel.dailyWeatherList.observe(viewLifecycleOwner) { dailyWeatherList ->
                dailyAdapter.updateData(dailyWeatherList)
                shimmerLayout.stopShimmer()
                shimmerLayout.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
        }
    }
}