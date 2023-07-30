package com.example.weatherapp.ui.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.data.SettingsRepository
import com.example.weatherapp.domain.models.HourlyWeather
import com.example.weatherapp.ui.adapters.HourlyWeatherAdapter
import com.example.weatherapp.ui.viewModels.HourlyWeatherViewModel
import com.facebook.shimmer.ShimmerFrameLayout

class HourlyWeatherFragment : Fragment() {

    companion object {
        fun newInstance() = HourlyWeatherFragment()
    }

    private lateinit var viewModel: HourlyWeatherViewModel
    private lateinit var shimmerLayout: ShimmerFrameLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_hourly_weather, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)[HourlyWeatherViewModel::class.java]
        // TODO: Use the ViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val settingsRepository = SettingsRepository(requireContext())
        val userLocation = settingsRepository.getSavedUserLocation()
        val recyclerView: RecyclerView = view.findViewById(R.id.hourlyWeatherRecyclerView)
        val adapter = HourlyWeatherAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
        viewModel = ViewModelProvider(this)[HourlyWeatherViewModel::class.java]

        shimmerLayout = view.findViewById(R.id.shimmer_view_container)
        shimmerLayout.visibility = View.VISIBLE;
        shimmerLayout.startShimmer()

        // Fetch the hourly weather data for the desired location
        if (userLocation != null) {
            viewModel.fetchHourlyWeather(userLocation)
        }

        // Observe the hourly weather data from the ViewModel
        viewModel.hourlyWeatherList.observe(viewLifecycleOwner) { hourlyWeatherList ->
            adapter.updateData(hourlyWeatherList)
            shimmerLayout.stopShimmer()
            shimmerLayout.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        // Start the shimmer effect when the fragment is visible
        shimmerLayout.visibility = View.VISIBLE;
        shimmerLayout.startShimmer()
    }

    override fun onPause() {
        super.onPause()
        shimmerLayout.visibility = View.GONE;
        shimmerLayout.stopShimmer()
    }
}