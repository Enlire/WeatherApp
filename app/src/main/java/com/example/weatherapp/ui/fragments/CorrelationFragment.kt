package com.example.weatherapp.ui.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.R
import com.example.weatherapp.data.networking.NetworkUtils
import com.example.weatherapp.domain.CorrelationCalculator
import com.example.weatherapp.domain.LocationServiceImpl
import com.example.weatherapp.ui.ErrorCallback
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
    private lateinit var correlationCalculator: CorrelationCalculator
    private lateinit var correlationHelpImageView: ImageView
    private lateinit var coefficientHelpImageView: ImageView

    private var xAxisNameTemp: TextView? = null
    private var xAxisNamePrecip: TextView? = null
    private var xValuesTemp = mutableListOf<Float>()
    private var yValuesTemp = mutableListOf<Float>()
    private val entriesTemp = ArrayList<Entry>()
    private var xValuesPrecip = mutableListOf<Float>()
    private var yValuesPrecip = mutableListOf<Float>()
    private val entriesPrecip = ArrayList<Entry>()
    private var observationCounter = 0


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
        correlationCalculator = CorrelationCalculator()
        //val locationService = LocationServiceImpl(requireContext())

        correlationHelpImageView = view.findViewById(R.id.correlationHelp)
        coefficientHelpImageView = view.findViewById(R.id.coefficientHelp)

        correlationHelpImageView.setOnClickListener {
            showDialog("Корреляция — это статистическая взаимосвязь двух или более случайных величин.")
        }

        coefficientHelpImageView.setOnClickListener {
            showDialog("Чем ближе коэффициент корреляции к +1 или -1, тем сильнее похожи погодные условия. Если коэффициент отрицательный, значит параметры одного города обратно пропорциональны параметрам другого города.\n" +
                    "\n" +
                    "Сила взаимосвзяи определяется по абсолютному значению коэффициента корреляции (r):\n" +
                    "- отсутствие корреляции (r = 0);\n" +
                    "- очень слабая корреляция (r ≤ 0.2);\n" +
                    "- слабая корреляция (r ≤ 0.5);\n" +
                    "- средняя корреляция (r ≤ 0.7);\n" +
                    "- сильная корреляция (r ≤ 0.9);\n" +
                    "- очень сильная корреляция (r > 0.9)")
        }

        // Setting up charts
        tempChartCardView = view.findViewById(R.id.tempChartCardView)
        precipChartCardView = view.findViewById(R.id.precipChartCardView)

        tempScatterChart = ScatterChart(requireContext())
        precipScatterChart = ScatterChart(requireContext())

        tempChartCardView.addView(tempScatterChart)
        precipChartCardView.addView(precipScatterChart)

        tempScatterChart.setNoDataText("")
        precipScatterChart.setNoDataText("")

        // Correlation coefficient
        xAxisNameTemp = TextView(requireContext())
        xAxisNamePrecip = TextView(requireContext())

        val params = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        params.gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
        params.setMargins(0, 30, 0, 20)

        tempChartCardView.addView(xAxisNameTemp, params)
        precipChartCardView.addView(xAxisNamePrecip, params)

        viewModel.correlationListTempCity1.observe(viewLifecycleOwner) { correlationTempList1 ->
            //Log.i("LiveDataUpdate", "Temp City 1 LiveData updated: $correlationTempList1")
            for (value in correlationTempList1) {
                xValuesTemp.add(value)
            }
            observationCounter++
            onObservationComplete()
        }
        viewModel.correlationListPrecipCity1.observe(viewLifecycleOwner) { correlationPrecipList1 ->
            for (value in correlationPrecipList1) {
                xValuesPrecip.add(value)
            }
            observationCounter++
            onObservationComplete()
        }
        viewModel.correlationListTempCity2.observe(viewLifecycleOwner) { correlationTempList2 ->
            //Log.i("LiveDataUpdate", "Temp City 2 LiveData updated: $correlationTempList2")
            for (value in correlationTempList2) {
                yValuesTemp.add(value)
            }
            observationCounter++
            onObservationComplete()
        }
        viewModel.correlationListPrecipCity2.observe(viewLifecycleOwner) { correlationPrecipList2 ->
            for (value in correlationPrecipList2) {
                yValuesPrecip.add(value)
            }
            observationCounter++
            onObservationComplete()
        }

        // Observe errors
//        viewModel.setErrorCallback(object : ErrorCallback {
//            override fun onError(errorMessage: String?) {
//                if (!errorMessage.isNullOrEmpty()) {
//                    DialogUtils.showAPIErrorDialog(childFragmentManager, errorMessage)
//                }
//            }
//        })

        view.findViewById<Button>(R.id.button)
            .setOnClickListener {
                val firstLocationName = firstLocationEditText.text.toString().trim()
                val secondLocationName = secondLocationEditText.text.toString().trim()

                if (firstLocationName.isEmpty() || secondLocationName.isEmpty()) {
                    showDialog("Пожалуйста, заполните оба поля.")
                } else {
                    observationCounter = 0
                    xValuesTemp.clear()
                    yValuesTemp.clear()
                    xValuesPrecip.clear()
                    yValuesPrecip.clear()
                    entriesTemp.clear()
                    entriesPrecip.clear()

                    constraintLayout.visibility = View.GONE
                    shimmerLayout.visibility = View.VISIBLE
                    shimmerLayout.startShimmer()

                    val firstLocationCoordinates: Pair<Double, Double> = Pair(0.0, 0.0)
                        //locationService.getCoordinatesFromAddress(firstLocationName)
                    val secondLocationCoordinates: Pair<Double, Double> =Pair(0.0, 0.0)
                        //locationService.getCoordinatesFromAddress(secondLocationName)

                    if ((firstLocationCoordinates.first == 0.0 && firstLocationCoordinates.second == 0.0) || (secondLocationCoordinates.first == 0.0 && secondLocationCoordinates.second == 0.0)) {
                        DialogUtils.showAPIErrorDialog(childFragmentManager, "Ошибка при получении данных. Попробуйте повторить запрос.")
                        shimmerLayout.stopShimmer()
                        shimmerLayout.visibility = View.GONE
                    }
                    else {
//                        viewModel.fetchCorrelationData(
//                            firstLocationCoordinates.first,
//                            firstLocationCoordinates.second,
//                            1
//                        )
//                        viewModel.fetchCorrelationData(
//                            secondLocationCoordinates.first,
//                            secondLocationCoordinates.second,
//                            2
//                        )
                    }
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
        if (observationCounter == 4) {
            for (i in xValuesTemp.indices) {
                val xTemp = xValuesTemp[i]
                val yTemp = yValuesTemp[i]
                val xPrecip = xValuesPrecip[i]
                val yPrecip = yValuesPrecip[i]
                entriesTemp.add(Entry(xTemp, yTemp))
                entriesPrecip.add(Entry(xPrecip, yPrecip))
            }
            entriesTemp.sortBy { it.x }
            entriesPrecip.sortBy { it.x }

            val correlationCoefficientTemp =
                correlationCalculator.calculateCorrelationCoefficient(xValuesTemp, yValuesTemp)
            val correlationCoefficientPrecip =
                correlationCalculator.calculateCorrelationCoefficient(
                    xValuesPrecip,
                    yValuesPrecip
                )

            updateChart(
                tempScatterChart,
                tempChartCardView,
                entriesTemp,
                R.color.min_chart,
                "°C",
                correlationCoefficientTemp
            )
            updateChart(
                precipScatterChart,
                precipChartCardView,
                entriesPrecip,
                R.color.max_chart,
                " мм",
                correlationCoefficientPrecip
            )

            shimmerLayout.stopShimmer()
            shimmerLayout.visibility = View.GONE
            constraintLayout.visibility = View.VISIBLE
            observationCounter = 0
        }
    }

    private fun updateChart(scatterChart: ScatterChart, cardView: CardView, entries: ArrayList<Entry>, colorId: Int, unit: String, coefficient: Float) {
        val dataSet = ScatterDataSet(entries, "")
        dataSet.color = ContextCompat.getColor(requireContext(), colorId)
        dataSet.setScatterShape(ScatterChart.ScatterShape.CIRCLE)
        dataSet.scatterShapeSize = 12f
        dataSet.setDrawValues(false)

        val data = ScatterData(dataSet)
        scatterChart.description.isEnabled = false

        // Axis settings
        scatterChart.axisLeft.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                return value.toInt().toString() + unit
            }
        }
        scatterChart.xAxis.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                return value.toInt().toString() + unit
            }
        }
        scatterChart.axisRight.setDrawGridLines(false)
        scatterChart.axisLeft.textColor = ContextCompat.getColor(requireContext(), R.color.main_text)
        scatterChart.axisLeft.axisMinimum = dataSet.yMin - 1
        scatterChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        scatterChart.xAxis.axisMinimum = dataSet.xMin - 1
        scatterChart.xAxis.textColor = ContextCompat.getColor(requireContext(), R.color.main_text)
        scatterChart.axisRight.isEnabled = false
        scatterChart.setTouchEnabled(false)

        // Correlation coefficient
        val xAxisName: TextView? = if (cardView == tempChartCardView) {
            xAxisNameTemp
        } else {
            xAxisNamePrecip
        }
        xAxisName?.textSize = 16f
        xAxisName?.setTextColor(ContextCompat.getColor(requireContext(), R.color.main_text))
        xAxisName?.text = null
        xAxisName?.text = "r = $coefficient"

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

        scatterChart.notifyDataSetChanged()
        scatterChart.data = data
        scatterChart.invalidate()
    }

    private fun showDialog(message: String) {
        val dialog = AlertDialog.Builder(requireContext())
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        dialog.show()
    }
}