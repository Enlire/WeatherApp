package com.example.weatherapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.data.networking.WeatherNetworkDataSource
import com.example.weatherapp.ui.ErrorCallback
import com.example.weatherapp.ui.adapters.DailyWeatherAdapter
import com.example.weatherapp.ui.dialogs.DialogUtils
import com.example.weatherapp.ui.viewModels.DailyWeatherViewModel
import com.example.weatherapp.ui.viewModelsFactories.DailyWeatherViewModelFactory
import com.facebook.shimmer.ShimmerFrameLayout
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class DailyWeatherFragment : ScopedFragment(), KodeinAware {
    override val kodein by closestKodein()
    private val dailyWeatherViewModelFactory: DailyWeatherViewModelFactory by instance()
    private val weatherNetworkDataSource: WeatherNetworkDataSource by instance()

    private lateinit var shimmerLayout: ShimmerFrameLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModelDaily: DailyWeatherViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_daily_weather, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModelDaily = ViewModelProvider(this, dailyWeatherViewModelFactory)[DailyWeatherViewModel::class.java]
        recyclerView = view.findViewById(R.id.dailyWeatherRecyclerView)
        val dailyAdapter = DailyWeatherAdapter(emptyList(), requireContext())
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = dailyAdapter

        shimmerLayout = view.findViewById(R.id.shimmer_view_container)
        shimmerLayout.visibility = View.VISIBLE
        shimmerLayout.startShimmer()

        observeDailyWeatherDataChanges(dailyAdapter)
    }

    private fun observeDailyWeatherDataChanges(dailyAdapter: DailyWeatherAdapter) = launch {
        val dailyWeather = viewModelDaily.dailyWeather.await()
        val weatherLocation = viewModelDaily.weatherLocation.await()

        dailyWeather.observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer
            dailyAdapter.updateData(it)
        })

        weatherLocation.observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer
        })

        viewModelDaily.fetchDailyWeatherData()

        shimmerLayout.stopShimmer()
        shimmerLayout.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE

        // Observe errors
        weatherNetworkDataSource.setErrorCallback(object : ErrorCallback {
            override fun onError(errorMessage: String?) {
                if (!errorMessage.isNullOrBlank()) {
                    requireActivity().runOnUiThread {
                        if (isAdded) {
                            DialogUtils.showAPIErrorDialog(childFragmentManager, errorMessage)
                            shimmerLayout.visibility = View.VISIBLE
                            shimmerLayout.startShimmer()
                            recyclerView.visibility = View.GONE
                        }
                    }
                }
            }
        })
    }
}