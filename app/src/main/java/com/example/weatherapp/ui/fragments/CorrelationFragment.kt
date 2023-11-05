package com.example.weatherapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.R
import com.example.weatherapp.data.networking.NetworkUtils
import com.example.weatherapp.domain.LocationService
import com.example.weatherapp.ui.dialogs.DialogUtils
import com.example.weatherapp.ui.viewModels.CorrelationViewModel
import com.facebook.shimmer.ShimmerFrameLayout
import com.github.mikephil.charting.charts.ScatterChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.ScatterData
import com.github.mikephil.charting.data.ScatterDataSet
import com.github.mikephil.charting.formatter.ValueFormatter


class CorrelationFragment : Fragment() {

    companion object {
        fun newInstance() = CorrelationFragment()
    }

    private lateinit var viewModel: CorrelationViewModel
    private lateinit var tempScatterChart: ScatterChart
    private lateinit var precipScatterChart: ScatterChart
    private lateinit var tempChartCardView: CardView
    private lateinit var precipChartCardView: CardView
    private lateinit var firstLocationEditText: EditText
    private lateinit var secondLocationEditText: EditText
    private lateinit var shimmerLayout: ShimmerFrameLayout
    private lateinit var constraintLayout: ConstraintLayout
    private var observationCount = 0
    private var xValues = mutableListOf<Int>()
    private var yValues = mutableListOf<Int>()
    private val entries = ArrayList<Entry>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_correlation, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CorrelationViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        shimmerLayout = view.findViewById(R.id.shimmer_view_container)
        constraintLayout = view.findViewById(R.id.chartsLayout)

        viewModel = ViewModelProvider(this)[CorrelationViewModel::class.java]
        firstLocationEditText = view.findViewById(R.id.firstLocationEditText)
        secondLocationEditText = view.findViewById(R.id.secondLocationEditText)
        val locationService = LocationService(requireContext())
        observationCount = 0

        // Setting up charts
        tempChartCardView = view.findViewById(R.id.tempChartCardView)
        precipChartCardView = view.findViewById(R.id.precipChartCardView)
        tempScatterChart = ScatterChart(requireContext())
        precipScatterChart = ScatterChart(requireContext())
        tempChartCardView.addView(tempScatterChart)
        tempChartCardView.addView(precipScatterChart)
        tempScatterChart.setNoDataText("")
        precipScatterChart.setNoDataText("")

        view.findViewById<Button>(R.id.button)
            .setOnClickListener {
                shimmerLayout.visibility = View.VISIBLE
                shimmerLayout.startShimmer()

                val firstLocationName = "Москва"//firstLocationEditText.text.toString()
                val secondLocationName = "Волгоград"//secondLocationEditText.text.toString()
                val firstLocationCoordinates: Pair<Double, Double> = locationService.getCoordinatesFromAddress(firstLocationName)
                val secondLocationCoordinates: Pair<Double, Double> = locationService.getCoordinatesFromAddress(secondLocationName)

                viewModel.fetchCorrelationData(firstLocationCoordinates.first, firstLocationCoordinates.second, 1)
                viewModel.correlationListCity1.observe(viewLifecycleOwner) { correlationList1 ->
                    //Log.i("correlationList1", correlationList1.toString())
                    for (value in correlationList1) {
                        xValues.add(value)
                    }
                    onObservationComplete()
                }

                viewModel.fetchCorrelationData(secondLocationCoordinates.first, secondLocationCoordinates.second,2)
                viewModel.correlationListCity2.observe(viewLifecycleOwner) { correlationList2 ->
                    //Log.i("correlationList2", correlationList2.toString())
                    for (value in correlationList2) {
                        yValues.add(value)
                    }
                    onObservationComplete()
                }
            }

        // Check Internet connection and display dialog if not available
        if (NetworkUtils.isInternetAvailable(requireContext())) {

        }
        else {
            DialogUtils.showNoInternetDialog(childFragmentManager)
        }
    }

    private fun onObservationComplete() {
        observationCount++

        if (observationCount == 2) {
            for (i in xValues.indices) {
                val x = xValues[i].toFloat()
                val y = yValues[i].toFloat()
                entries.add(Entry(x, y))
            }
            entries.sortBy { it.x }
            updateTempChart(tempScatterChart, tempChartCardView, entries, R.color.min_chart)

            shimmerLayout.stopShimmer()
            shimmerLayout.visibility = View.GONE
            constraintLayout.visibility = View.VISIBLE

            //Log.i("entries", entries.toString())
        }
    }

    private fun updateTempChart(scatterChart: ScatterChart, cardView: CardView, entries: ArrayList<Entry>, colorId: Int) {

        val dataSet = ScatterDataSet(entries, "")
        dataSet.color = ContextCompat.getColor(requireContext(), colorId)
        dataSet.setScatterShape(ScatterChart.ScatterShape.CIRCLE)
        dataSet.scatterShapeSize = 14f
        dataSet.setDrawValues(false)

        val data = ScatterData(dataSet)
        scatterChart.data = data
        scatterChart.description.isEnabled = false

        // Axis settings
        scatterChart.axisLeft.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                return value.toInt().toString() + "°C"
            }
        }
        scatterChart.xAxis.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                return value.toInt().toString() + "°C"
            }
        }
        scatterChart.axisRight.setDrawGridLines(false)
        scatterChart.axisLeft.textColor = ContextCompat.getColor(requireContext(), R.color.main_text)
        scatterChart.axisLeft.axisMinimum = dataSet.yMin - 1
        //scatterChart.axisLeft.setDrawGridLines(false)
        scatterChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        scatterChart.xAxis.axisMinimum = dataSet.xMin - 1
        //scatterChart.xAxis.setDrawGridLines(false)
        scatterChart.xAxis.textColor = ContextCompat.getColor(requireContext(), R.color.main_text)
        scatterChart.axisRight.isEnabled = false
        scatterChart.setTouchEnabled(false)

        // Correlation coefficient
        var xAxisName = TextView(activity)
        xAxisName.text = "r = "
        xAxisName.textSize = 16f
        xAxisName.setTextColor(ContextCompat.getColor(requireContext(), R.color.main_text))
        val params = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        params.gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
        params.setMargins(0, 20, 0, 20)
        cardView.addView(xAxisName, params)

        // Padding settings
        val paddingDp = 6
        val density = resources.displayMetrics.density
        val padding = (paddingDp * density)
        val paddingLeft = (3 * density)
        val paddingBottom = 10 * density
        scatterChart.setExtraOffsets(paddingLeft, padding, padding, paddingBottom)

        // Legend Settings
        val legend = scatterChart.legend
        legend.isEnabled = false

        scatterChart.invalidate()
    }
}