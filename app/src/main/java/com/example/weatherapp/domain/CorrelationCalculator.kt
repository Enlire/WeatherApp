package com.example.weatherapp.domain

import kotlin.math.sqrt

class CorrelationCalculator {
    fun calculateCorrelationCoefficient(xValues: List<Float>, yValues: List<Float>): Float {
        if (xValues.size != yValues.size || xValues.isEmpty()) {
            return Float.NaN
        }

        val n = xValues.size

        val meanX = xValues.average()
        val meanY = yValues.average()

        var covariance = 0.0
        var varianceX = 0.0
        var varianceY = 0.0

        for (i in 0 until n) {
            val deviationX = xValues[i] - meanX
            val deviationY = yValues[i] - meanY
            covariance += deviationX * deviationY
            varianceX += deviationX * deviationX
            varianceY += deviationY * deviationY
        }

        val correlation = covariance / (sqrt(varianceX) * sqrt(varianceY))

        return correlation.toFloat()
    }

}