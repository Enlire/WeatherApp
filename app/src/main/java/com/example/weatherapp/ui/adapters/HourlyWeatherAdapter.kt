package com.example.weatherapp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.domain.models.HourlyWeather

class HourlyWeatherAdapter(var hourlyWeatherList: List<HourlyWeather>) :
    RecyclerView.Adapter<HourlyWeatherAdapter.HourlyWeatherViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyWeatherViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.hourly_card, parent, false)
        return HourlyWeatherViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: HourlyWeatherViewHolder, position: Int) {
        val hourlyWeather = hourlyWeatherList[position]

        // Bind the views with the corresponding data
        holder.dayTextView.text = hourlyWeather.day.toString()
        holder.hourTextView.text = hourlyWeather.hour.toString()
        holder.tempTextView.text = hourlyWeather.temperature.toString()
        holder.conditionTextView.text = hourlyWeather.description.toString()
        holder.chanceTextView.text = hourlyWeather.chance_of_rain.toString()
        holder.precipTextView.text = hourlyWeather.precip.toString()
        holder.cloudTextView.text = hourlyWeather.cloud.toString()
        holder.humidityTextView.text = hourlyWeather.humidity.toString()
        holder.windTextView.text = "${hourlyWeather.wind_speed} ${hourlyWeather.wind_dir}"
        holder.pressureTextView.text = hourlyWeather.pressure.toString()
        holder.uvTextView.text = hourlyWeather.uv_index.toString()
        holder.dewPointTextView.text = hourlyWeather.dew_point.toString()
    }

    override fun getItemCount(): Int {
        return hourlyWeatherList.size
    }

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
}