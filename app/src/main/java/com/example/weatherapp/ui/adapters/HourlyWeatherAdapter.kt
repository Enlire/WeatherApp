package com.example.weatherapp.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.domain.models.HourlyWeather
import com.example.weatherapp.domain.models.WeatherCondition

class HourlyWeatherAdapter(private var hourlyWeatherList: List<HourlyWeather>) :
    RecyclerView.Adapter<HourlyWeatherAdapter.HourlyWeatherViewHolder>() {

    class HourlyWeatherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dayTextView: TextView = itemView.findViewById(R.id.day)
        val hourTextView: TextView = itemView.findViewById(R.id.hour)
        val tempTextView: TextView = itemView.findViewById(R.id.temp)
        val conditionTextView: TextView = itemView.findViewById(R.id.condition)
        val chanceTextView: TextView = itemView.findViewById(R.id.chance_of_precip)
        val precipTextView: TextView = itemView.findViewById(R.id.precip)
        val cloudTextView: TextView = itemView.findViewById(R.id.cloud)
        val humidityTextView: TextView = itemView.findViewById(R.id.humidity)
        val windTextView: TextView = itemView.findViewById(R.id.wind)
        val pressureTextView: TextView = itemView.findViewById(R.id.pressure)
        val uvTextView: TextView = itemView.findViewById(R.id.uv)
        val dewPointTextView: TextView = itemView.findViewById(R.id.dew_point)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyWeatherViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.hourly_card, parent, false)
        return HourlyWeatherViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: HourlyWeatherViewHolder, position: Int) {
        val hourlyWeather = hourlyWeatherList[position]
        val weatherDescription = WeatherCondition()

        // Bind the views with the corresponding data
        holder.dayTextView.text = hourlyWeather.day
        holder.hourTextView.text = hourlyWeather.hour
        holder.tempTextView.text = "${hourlyWeather.temperature}°C"
        holder.conditionTextView.text = hourlyWeather.description
        holder.chanceTextView.text = "${hourlyWeather.chance_of_rain}%"
        holder.precipTextView.text = "${hourlyWeather.precip} мм"
        holder.cloudTextView.text = "${hourlyWeather.cloud}%"
        holder.humidityTextView.text = "${hourlyWeather.humidity}%"
        holder.windTextView.text = "${hourlyWeather.wind_speed} м/с ${weatherDescription.translateWindDir(hourlyWeather.wind_dir)}"
        holder.pressureTextView.text = "${hourlyWeather.pressure} мм рт. ст."
        holder.uvTextView.text = hourlyWeather.uv_index.toString()
        holder.dewPointTextView.text = "${hourlyWeather.dew_point}°C"
    }

    override fun getItemCount(): Int {
        return hourlyWeatherList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newHourlyWeatherList: List<HourlyWeather>) {
        hourlyWeatherList = newHourlyWeatherList
        notifyDataSetChanged()
    }
}