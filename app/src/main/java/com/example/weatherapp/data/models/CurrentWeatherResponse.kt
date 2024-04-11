package com.example.weatherapp.data.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

//const val CURRENT_WEATHER_ID = 0

//@Entity(tableName = "current_weather")
data class CurrentWeatherResponse(
	//@Embedded(prefix = "current_")
	@field:SerializedName("current")
	val current: Current,

	//@Embedded(prefix = "location_")
	@field:SerializedName("location")
	val location: Location,

	//@PrimaryKey(autoGenerate = false)
	//val id: Int = CURRENT_WEATHER_ID
)

data class Condition(
	@field:SerializedName("text")
	val text: String,

	@field:SerializedName("code")
	val code: Int
)

data class Location(
	@field:SerializedName("localtime")
	val localtime: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("lon")
	val lon: Double,

	@field:SerializedName("lat")
	val lat: Double
)

data class Current(
	@field:SerializedName("last_updated")
	val lastUpdated: String,

	@field:SerializedName("feelslike_c")
	val feelslikeC: Double,

	@field:SerializedName("uv")
	val uv: Double,

	@field:SerializedName("is_day")
	val isDay: Int,

	@field:SerializedName("wind_dir")
	val windDir: String,

	@field:SerializedName("temp_c")
	val tempC: Double,

	@field:SerializedName("pressure_in")
	val pressureIn: Double,

	@field:SerializedName("wind_kph")
	val windKph: Double,

	//@Embedded(prefix = "condition_")
	@field:SerializedName("condition")
	val condition: Condition,

	@field:SerializedName("vis_km")
	val visKm: Double,

	@field:SerializedName("humidity")
	val humidity: Int,
	val icResId: Int
)
