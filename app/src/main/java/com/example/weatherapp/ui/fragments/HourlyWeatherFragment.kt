package com.example.weatherapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.data.networking.NetworkUtils
import com.example.weatherapp.domain.LocationService
import com.example.weatherapp.ui.ErrorCallback
import com.example.weatherapp.ui.adapters.HourlyWeatherAdapter
import com.example.weatherapp.ui.dialogs.DialogUtils
import com.example.weatherapp.ui.viewModels.HourlyWeatherViewModel
import com.facebook.shimmer.ShimmerFrameLayout

class HourlyWeatherFragment : Fragment() {

    companion object {
        fun newInstance() = HourlyWeatherFragment()
    }

    private lateinit var viewModel: HourlyWeatherViewModel
    private lateinit var shimmerLayout: ShimmerFrameLayout
    private lateinit var recyclerView: RecyclerView
    private val sharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_hourly_weather, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)[HourlyWeatherViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val locationService = LocationService(requireContext())
        val locationData: Triple<Double, Double, String> = locationService.getLocation()
        val (latitude, longitude, locationName) = locationData

        recyclerView = view.findViewById(R.id.hourlyWeatherRecyclerView)
        val hourlyAdapter = HourlyWeatherAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = hourlyAdapter
        viewModel = ViewModelProvider(this)[HourlyWeatherViewModel::class.java]

        shimmerLayout = view.findViewById(R.id.shimmer_view_container)
        shimmerLayout.visibility = View.VISIBLE
        shimmerLayout.startShimmer()

        // Check Internet connection and display dialog if not available
        if (NetworkUtils.isInternetAvailable(requireContext())) {
            checkLocationSettings(locationName, latitude, longitude, hourlyAdapter)
        }
        else {
            DialogUtils.showNoInternetDialog(childFragmentManager)
        }
    }

    private fun checkLocationSettings(
        locationName: String,
        latitude: Double,
        longitude: Double,
        hourlyAdapter: HourlyWeatherAdapter
    ) {
        val locationService = LocationService(requireContext())
        val isSwitchEnabled = sharedPreferences.getBoolean("USE_DEVICE_LOCATION", false)

        if (isSwitchEnabled && !locationService.isLocationServiceEnabled()) {
            val fragmentId = R.id.hourly
            DialogUtils.showLocationEnableDialog(childFragmentManager, fragmentId)
        } else {
            // Fetch the hourly weather data for the desired location
            viewModel.fetchHourlyWeather(locationName, latitude, longitude)

            // Observe the hourly weather data from the ViewModel
            viewModel.hourlyWeatherList.observe(viewLifecycleOwner) { hourlyWeatherList ->
                hourlyAdapter.updateData(hourlyWeatherList)
                shimmerLayout.stopShimmer()
                shimmerLayout.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }

            // Observe errors
            viewModel.setErrorCallback(object : ErrorCallback {
                override fun onError(errorMessage: String?) {
                    if (!errorMessage.isNullOrEmpty()) {
                        if (isAdded) {
                            DialogUtils.showAPIErrorDialog(childFragmentManager, errorMessage)
                            shimmerLayout.visibility = View.VISIBLE
                            shimmerLayout.startShimmer()
                            recyclerView.visibility = View.GONE
                        }
                    }
                }
            })
        }
    }
}