package com.example.weatherapp.ui

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.weatherapp.R

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        // TODO: Use the ViewModel
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        viewModel = MainViewModel()
//        subscribe()
//
//        etCityName = view.findViewById(R.id.et_city_name)
//        tvResult = view.findViewById(R.id.tv_result)
//        btnSend = view.findViewById(R.id.btn_send_request)
//
//        // Add on click button to the send button
//        btnSend.setOnClickListener {
//            // Text field validation
//            if (etCityName.text.isNullOrEmpty() or etCityName.text.isNullOrBlank()) {
//                etCityName.error = "Field can't be null"
//            } else {
//                // Get weather data
//                viewModel.getCurrentWeatherData(etCityName.text.toString())
//
//                viewModel.currentWeatherData.observe(viewLifecycleOwner) { currentWeatherData ->
//                    // Display weather data to the UI
//                    setResultText(currentWeatherData)
//                }
//            }
//        }
//    }
//
//    private fun subscribe() {
//        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
//            // Set the result text to Loading
//            if (isLoading) tvResult.text = resources.getString(R.string.loading)
//        }
//
//        viewModel.isError.observe(viewLifecycleOwner) { isError ->
//            // Hide display image and set the result text to the error message
//            if (isError) {
//                tvResult.text = viewModel.errorMessage
//            }
//        }
//
//        viewModel.currentWeatherData.observe(viewLifecycleOwner) { currentWeatherData ->
//            // Display weather data to the UI
//            setResultText(currentWeatherData)
//        }
//    }
//
//    private fun setResultText(currentWeatherData: CurrentWeatherResponse) {
//        val resultText = StringBuilder("Result:\n")
//
//        currentWeatherData.location.let { location ->
//            resultText.append("Name: ${location?.name}\n")
//        }
//
//        currentWeatherData.current.let { current ->
//            current?.condition.let { condition ->
//                resultText.append("Condition: ${condition?.text}\n")
//            }
//            resultText.append("Celcius: ${current?.tempC}\n")
//        }
//
//        tvResult.text = resultText
//    }
}