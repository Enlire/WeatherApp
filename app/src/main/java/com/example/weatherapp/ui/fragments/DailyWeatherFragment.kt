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
import com.example.weatherapp.ui.adapters.DailyWeatherAdapter
import com.example.weatherapp.ui.viewModels.DailyWeatherViewModel
import com.facebook.shimmer.ShimmerFrameLayout

class DailyWeatherFragment : Fragment() {

    companion object {
        fun newInstance() = DailyWeatherFragment()
    }

    private lateinit var viewModel: DailyWeatherViewModel
    private lateinit var shimmerLayout: ShimmerFrameLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_daily_weather, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.dailyWeatherRecyclerView)
        val adapter = DailyWeatherAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
        viewModel = ViewModelProvider(this)[DailyWeatherViewModel::class.java]

        shimmerLayout = view.findViewById(R.id.shimmer_view_container)
        shimmerLayout.visibility = View.VISIBLE;
        shimmerLayout.startShimmer()

        // Observe the hourly weather data from the ViewModel
        viewModel.dailyWeatherList.observe(viewLifecycleOwner) { dailyWeatherList ->
            adapter.updateData(dailyWeatherList)
            shimmerLayout.stopShimmer()
            shimmerLayout.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }

        // Fetch the hourly weather data for the desired location
        viewModel.fetchDailyWeather()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(DailyWeatherViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onResume() {
        super.onResume()

        shimmerLayout.visibility = View.VISIBLE;
        shimmerLayout.startShimmer()
    }

    override fun onPause() {
        super.onPause()
        shimmerLayout.visibility = View.GONE;
        shimmerLayout.stopShimmer()
    }
}