package com.example.weatherapp.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.domain.models.DailyWeather

class DailyWeatherAdapter(private var dailyWeatherList: List<DailyWeather>) :
    RecyclerView.Adapter<DailyWeatherAdapter.DailyWeatherViewHolder>() {

    class DailyWeatherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTextView: TextView = itemView.findViewById(R.id.date)
        val dayTextView: TextView = itemView.findViewById(R.id.day)
        val tempTextView: TextView = itemView.findViewById(R.id.temp)
        val conditionTextView: TextView = itemView.findViewById(R.id.condition)
        val iconImageView: ImageView = itemView.findViewById(R.id.imageView)
        val chanceTextView: TextView = itemView.findViewById(R.id.chance_of_precip)
        val precipTextView: TextView = itemView.findViewById(R.id.precip)
        val windTextView: TextView = itemView.findViewById(R.id.wind)
        val uvTextView: TextView = itemView.findViewById(R.id.uv)
        val sunriseTextView: TextView = itemView.findViewById(R.id.sunrise)
        val sunsetTextView: TextView = itemView.findViewById(R.id.sunset)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyWeatherViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.daily_card, parent, false)
        return DailyWeatherViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DailyWeatherViewHolder, position: Int) {
        val dailyWeather = dailyWeatherList[position]

        // Bind the views with the corresponding data
        holder.dateTextView.text = dailyWeather.date
        holder.dayTextView.text = dailyWeather.day
        holder.tempTextView.text = "${dailyWeather.temp_min}°C / ${dailyWeather.temp_max}°C"
        holder.conditionTextView.text = dailyWeather.description
        holder.iconImageView.setImageResource(dailyWeather.icResId)
        holder.chanceTextView.text = "${dailyWeather.chance_of_precip}%"
        holder.precipTextView.text = "${dailyWeather.precip} мм"
        holder.windTextView.text = "${dailyWeather.wind_speed} м/с"
        holder.uvTextView.text = dailyWeather.uv_index.toString()
        holder.sunriseTextView.text = dailyWeather.sunrise
        holder.sunsetTextView.text = dailyWeather.sunset
    }

    override fun getItemCount(): Int {
        return dailyWeatherList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newDailyWeatherList: List<DailyWeather>) {
        dailyWeatherList = newDailyWeatherList
        notifyDataSetChanged()
    }
}