package com.example.weatherapp.data.models

import com.google.gson.annotations.SerializedName

data class DailyWeatherResponse(

	@field:SerializedName("timezone")
	val timezone: String,

	@field:SerializedName("latitude")
	val latitude: Double,

	@field:SerializedName("daily")
	val daily: Daily,

	@field:SerializedName("utc_offset_seconds")
	val utcOffsetSeconds: Int,

	@field:SerializedName("longitude")
	val longitude: Double
)

data class Daily(

	@field:SerializedName("sunrise")
	val sunrise: List<String>,

	@field:SerializedName("weathercode")
	val weathercode: List<Int>,

	@field:SerializedName("uv_index_max")
	val uvIndexMax: List<Double>,

	@field:SerializedName("sunset")
	val sunset: List<String>,

	@field:SerializedName("precipitation_probability_max")
	val precipitationProbabilityMax: List<Int>,

	@field:SerializedName("temperature_2m_max")
	val temperature2mMax: List<Double>,

	@field:SerializedName("temperature_2m_min")
	val temperature2mMin: List<Double>,

	@field:SerializedName("time")
	val time: List<String>,

	@field:SerializedName("windspeed_10m_max")
	val windspeed10mMax: List<Double>,

	@field:SerializedName("precipitation_sum")
	val precipitationSum: List<Double>
)
