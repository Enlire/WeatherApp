package com.example.weatherapp.data.models

import androidx.room.Entity
import com.google.gson.annotations.SerializedName

data class HourlyWeatherResponse(
    @field:SerializedName("location")
	val location: WeatherLocation,

    @field:SerializedName("forecast")
	val forecast: Forecast
)

data class HourItem(

	@field:SerializedName("temp_c")
	val tempC: Double,

	@field:SerializedName("cloud")
	val cloud: Int,

	@field:SerializedName("wind_kph")
	val windKph: Double,

	@field:SerializedName("humidity")
	val humidity: Int,

	@field:SerializedName("uv")
	val uv: Double,

	@field:SerializedName("dewpoint_c")
	val dewpointC: Double,

	@field:SerializedName("is_day")
	val isDay: Int,

	@field:SerializedName("wind_dir")
	val windDir: String,

	@field:SerializedName("pressure_in")
	val pressureIn: Double,

	@field:SerializedName("chance_of_rain")
	val chanceOfRain: Int,

	@field:SerializedName("precip_mm")
	val precipMm: Double,

	@field:SerializedName("condition")
	val condition: Condition,

	@field:SerializedName("vis_km")
	val visKm: Double,

	@field:SerializedName("time")
	val time: String,

	@field:SerializedName("chance_of_snow")
	val chanceOfSnow: Int
)

data class Day(
	@field:SerializedName("maxtemp_c")
	val maxtempC: Double,

	@field:SerializedName("mintemp_c")
	val mintempC: Double
)

data class Forecast(

	@field:SerializedName("forecastday")
	val forecastday: List<ForecastdayItem>
)

data class ForecastdayItem(

	@field:SerializedName("hour")
	val hour: List<HourItem>,

	@field:SerializedName("day")
	val day: Day
)
