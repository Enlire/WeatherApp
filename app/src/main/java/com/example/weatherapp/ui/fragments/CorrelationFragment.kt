package com.example.weatherapp.ui.fragments

import android.annotation.SuppressLint
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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.weatherapp.R
import com.example.weatherapp.data.networking.WeatherNetworkDataSource
import com.example.weatherapp.domain.CorrelationCalculator
import com.example.weatherapp.domain.LocationServiceImpl
import com.example.weatherapp.ui.ErrorCallback
import com.example.weatherapp.ui.VerticalTextView
import com.example.weatherapp.ui.dialogs.DialogUtils
import com.example.weatherapp.ui.viewModels.CorrelationViewModel
import com.example.weatherapp.ui.viewModelsFactories.CorrelationViewModelFactory
import com.facebook.shimmer.ShimmerFrameLayout
import com.github.mikephil.charting.charts.ScatterChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.ScatterData
import com.github.mikephil.charting.data.ScatterDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import kotlin.math.abs
import kotlin.math.roundToInt


class CorrelationFragment : ScopedFragment(), KodeinAware {
    override val kodein by closestKodein()
    private val correlationViewModelFactory: CorrelationViewModelFactory by instance()
    private val weatherNetworkDataSource: WeatherNetworkDataSource by instance()

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
    private lateinit var tempCity1: TextView
    private lateinit var tempCity2: VerticalTextView
    private lateinit var precipCity1: TextView
    private lateinit var precipCity2: VerticalTextView
    private lateinit var textTemp: TextView
    private lateinit var textPrecip: TextView
    private lateinit var tempCoefficient: TextView

    private var xAxisNameTemp: TextView? = null
    private var xAxisNamePrecip: TextView? = null
    private var xValuesTemp = mutableListOf<Float>()
    private var yValuesTemp = mutableListOf<Float>()
    private val entriesTemp = ArrayList<Entry>()
    private var xValuesPrecip = mutableListOf<Float>()
    private var yValuesPrecip = mutableListOf<Float>()
    private val entriesPrecip = ArrayList<Entry>()
    private var observationCounter = 0
    private lateinit var firstLocationName: String
    private lateinit var secondLocationName: String

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (::firstLocationName.isInitialized) {
            outState.putString("first_location_name", firstLocationName)
        }
        if (::secondLocationName.isInitialized) {
            outState.putString("second_location_name", secondLocationName)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_correlation, container, false)
        shimmerLayout = view.findViewById(R.id.shimmer_view_container)
        constraintLayout = view.findViewById(R.id.chartsLayout)
        firstLocationEditText = view.findViewById(R.id.firstLocationEditText)
        secondLocationEditText = view.findViewById(R.id.secondLocationEditText)
        tempCity1 = view.findViewById(R.id.city1)
        tempCity2 = view.findViewById(R.id.city2)
        precipCity1 = view.findViewById(R.id.cityPrecip1)
        precipCity2 = view.findViewById(R.id.cityPrecip2)
        textTemp = view.findViewById(R.id.textTemp)
        textPrecip = view.findViewById(R.id.textPrecip)
        tempCoefficient = view.findViewById(R.id.coefficient)
        correlationHelpImageView = view.findViewById(R.id.correlationHelp)
        coefficientHelpImageView = view.findViewById(R.id.coefficientHelp)
        tempChartCardView = view.findViewById(R.id.tempChartCardView)
        precipChartCardView = view.findViewById(R.id.precipChartCardView)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        weatherNetworkDataSource.resetErrorCount()

        viewModel = ViewModelProvider(this, correlationViewModelFactory)[CorrelationViewModel::class.java]

        if (savedInstanceState != null) {
            firstLocationName = savedInstanceState.getString("first_location_name", "")
            secondLocationName = savedInstanceState.getString("second_location_name", "")
        }

        correlationCalculator = CorrelationCalculator()
        val locationService = LocationServiceImpl(requireContext())

        correlationHelpImageView.setOnClickListener {
            showDialog("Корреляция — это статистическая взаимосвязь двух или более случайных величин.")
        }

        coefficientHelpImageView.setOnClickListener {
            showDialog(
                "Чем ближе коэффициент корреляции к +1 или -1, тем сильнее похожи погодные условия. Если коэффициент отрицательный, значит параметры одного города обратно пропорциональны параметрам другого города.\n" +
                        "\n" +
                        "Сила взаимосвзяи определяется по абсолютному значению коэффициента корреляции (r):\n" +
                        "- отсутствие корреляции (r = 0);\n" +
                        "- очень слабая корреляция (r ≤ 0.2);\n" +
                        "- слабая корреляция (r ≤ 0.5);\n" +
                        "- средняя корреляция (r ≤ 0.7);\n" +
                        "- сильная корреляция (r ≤ 0.9);\n" +
                        "- очень сильная корреляция (r > 0.9)"
            )
        }

        // Setting up charts
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
        params.setMargins(15, 40, 10, 40)

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
        viewModel.setErrorCallback(object : ErrorCallback {
            override fun onError(errorMessage: String?) {
                if (!errorMessage.isNullOrEmpty()) {
                    DialogUtils.showAPIErrorDialog(childFragmentManager, errorMessage)
                }
            }
        })

        view.findViewById<Button>(R.id.button)
            .setOnClickListener {
                firstLocationName = firstLocationEditText.text.toString().trim()
                secondLocationName = secondLocationEditText.text.toString().trim()

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

                    val firstLocationCoordinates: Pair<Double, Double> =
                        locationService.getCoordinatesFromAddress(firstLocationName)
                    val secondLocationCoordinates: Pair<Double, Double> =
                        locationService.getCoordinatesFromAddress(secondLocationName)

                    if ((firstLocationCoordinates.first == 0.0 && firstLocationCoordinates.second == 0.0) || (secondLocationCoordinates.first == 0.0 && secondLocationCoordinates.second == 0.0)) {
                        DialogUtils.showAPIErrorDialog(
                            childFragmentManager,
                            "Ошибка при получении данных. Попробуйте повторить запрос."
                        )
                        shimmerLayout.stopShimmer()
                        shimmerLayout.visibility = View.GONE
                    } else {
                        lifecycleScope.launch {
                            viewModel.getCorrelationData(
                                firstLocationCoordinates.first,
                                firstLocationCoordinates.second,
                                1
                            )
                            viewModel.getCorrelationData(
                                secondLocationCoordinates.first,
                                secondLocationCoordinates.second,
                                2
                            )
                        }
                    }
                }
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
                correlationCoefficientTemp,
                tempCity1,
                tempCity2,
                textTemp
            )
            updateChart(
                precipScatterChart,
                precipChartCardView,
                entriesPrecip,
                R.color.max_chart,
                " мм",
                correlationCoefficientPrecip,
                precipCity1,
                precipCity2,
                textPrecip
            )

            shimmerLayout.stopShimmer()
            shimmerLayout.visibility = View.GONE
            constraintLayout.visibility = View.VISIBLE
            observationCounter = 0
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateChart(
        scatterChart: ScatterChart,
        cardView: CardView,
        entries: ArrayList<Entry>,
        colorId: Int,
        unit: String,
        coefficient: Float,
        city1: TextView,
        city2: TextView,
        text: TextView
    ) {
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
        val paddingLeft = (6 * density)
        val paddingBottom = 18 * density
        scatterChart.setExtraOffsets(paddingLeft, paddingBottom, padding, paddingBottom)

        // Legend Settings
        val legend = scatterChart.legend
        legend.isEnabled = false

        // Texts update
        city1.text = firstLocationName
        city2.text = secondLocationName
        text.text = "Данные совпадают на ${abs(coefficient * 100).roundToInt()}% "
        if (coefficient >= 0)
            text.append("(прямая зависимость)")
        else
            text.append("(обратная зависимость)")

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