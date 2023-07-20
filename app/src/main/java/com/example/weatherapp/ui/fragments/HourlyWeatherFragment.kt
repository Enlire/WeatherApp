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
import com.example.weatherapp.domain.models.HourlyWeather
import com.example.weatherapp.ui.adapters.HourlyWeatherAdapter
import com.example.weatherapp.ui.viewModels.HourlyWeatherViewModel

class HourlyWeatherFragment : Fragment() {

    companion object {
        fun newInstance() = HourlyWeatherFragment()
    }

    private lateinit var viewModel: HourlyWeatherViewModel
    private lateinit var adapter: HourlyWeatherAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_hourly_weather, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(HourlyWeatherViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize ViewModel
        viewModel = ViewModelProvider(this).get(HourlyWeatherViewModel::class.java)

        // Set up RecyclerView and Adapter
        val recyclerView = view.findViewById<RecyclerView>(R.id.hourlyWeatherRecyclerView)
        adapter = HourlyWeatherAdapter(emptyList()) // Pass an empty list for now
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Observe the hourly weather data from the ViewModel
        viewModel.getHourlyWeatherData().observe(viewLifecycleOwner) { hourlyWeatherList ->
            adapter.hourlyWeatherList = hourlyWeatherList // Update the data directly in the adapter
            adapter.notifyDataSetChanged() // Notify the adapter of the data change
        }

        // Fetch the hourly weather data for the desired location
        viewModel.fetchHourlyWeather("Your Location") // Replace "Your Location" with the desired location
    }
}