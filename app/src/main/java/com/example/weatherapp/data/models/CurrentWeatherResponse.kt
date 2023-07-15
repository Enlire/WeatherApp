package com.example.weatherapp.data.models

import com.google.gson.annotations.SerializedName

data class CurrentWeatherResponse(

	@field:SerializedName("current")
	val current: Current? = null,

	@field:SerializedName("location")
	val location: Location? = null
)

data class Condition(

	@field:SerializedName("text")
	val text: String? = null
)

data class Location(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("lon")
	val lon: Any? = null,

	@field:SerializedName("lat")
	val lat: Any? = null
)

data class Current(

	@field:SerializedName("feelslike_c")
	val feelslikeC: Any? = null,

	@field:SerializedName("uv")
	val uv: Any? = null,

	@field:SerializedName("wind_dir")
	val windDir: String? = null,

	@field:SerializedName("temp_c")
	val tempC: Any? = null,

	@field:SerializedName("pressure_in")
	val pressureIn: Any? = null,

	@field:SerializedName("precip_mm")
	val precipMm: Any? = null,

	@field:SerializedName("wind_kph")
	val windKph: Any? = null,

	@field:SerializedName("condition")
	val condition: Condition? = null,

	@field:SerializedName("vis_km")
	val visKm: Any? = null,

	@field:SerializedName("humidity")
	val humidity: Int? = null
)
