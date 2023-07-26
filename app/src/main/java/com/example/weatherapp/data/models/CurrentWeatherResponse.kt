package com.example.weatherapp.data.models

import com.google.gson.annotations.SerializedName

data class CurrentWeatherResponse(

	@field:SerializedName("current")
	val current: Current,

	@field:SerializedName("location")
	val location: Location
)

data class Condition(

	@field:SerializedName("text")
	val text: String,

	@field:SerializedName("icon")
	val icon: String,

	@field:SerializedName("code")
	val code: Int
)

data class Location(

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("lon")
	val lon: Double,

	@field:SerializedName("lat")
	val lat: Double
)

data class Current(

	@field:SerializedName("feelslike_c")
	val feelslikeC: Double,

	@field:SerializedName("uv")
	val uv: Double,

	@field:SerializedName("isDay")
	val isDay: Int,

	@field:SerializedName("wind_dir")
	val windDir: String,

	@field:SerializedName("temp_c")
	val tempC: Double,

	@field:SerializedName("pressure_in")
	val pressureIn: Double,

	@field:SerializedName("wind_kph")
	val windKph: Double,

	@field:SerializedName("condition")
	val condition: Condition,

	@field:SerializedName("vis_km")
	val visKm: Double,

	@field:SerializedName("humidity")
	val humidity: Int
)
