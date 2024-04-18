package com.example.weatherapp.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime

const val WEATHER_LOCATION_ID = 0

data class CurrentWeatherResponse(

	@field:SerializedName("current")
	val current: Current,

	@field:SerializedName("location")
	val location: WeatherLocation,
)

data class Condition(

	@field:SerializedName("text")
	val text: String,

	@field:SerializedName("code")
	val code: Int
)

@Entity(tableName = "weather_location")
data class WeatherLocation(

	@PrimaryKey(autoGenerate = false)
	var id: Int = WEATHER_LOCATION_ID,

	@field:SerializedName("localtime")
	val localtime: String,

	@field:SerializedName("localtime_epoch")
	val localTimeEpoch: Long,

	@field:SerializedName("tz_id")
	val tzId: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("lon")
	val lon: Double,

	@field:SerializedName("lat")
	val lat: Double
) {
	val zonedDateTime: ZonedDateTime
		get() {
			val instant = Instant.ofEpochSecond(localTimeEpoch)
			val zoneId = ZoneId.of(tzId)
			return ZonedDateTime.ofInstant(instant, zoneId)
		}
}

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

	@field:SerializedName("condition")
	val condition: Condition,

	@field:SerializedName("vis_km")
	val visKm: Double,

	@field:SerializedName("humidity")
	val humidity: Int,

	val icResId: Int
)
