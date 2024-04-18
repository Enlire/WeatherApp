package com.example.weatherapp.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.data.mappers.HourlyWeatherMapper
import com.example.weatherapp.domain.models.HourlyWeather

class HourlyCardsAdapter(private var hourlyWeatherList: List<HourlyWeather>) :
    RecyclerView.Adapter<HourlyCardsAdapter.HourlyCardsViewHolder>() {
    private val hourlyWeatherMapper = HourlyWeatherMapper()
    class HourlyCardsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val hourTextView: TextView = itemView.findViewById(R.id.hour)
        val tempTextView: TextView = itemView.findViewById(R.id.temp)
        val conditionTextView: TextView = itemView.findViewById(R.id.condition)
        val iconImageView: ImageView = itemView.findViewById(R.id.hourlyCardIcon)
        val chanceTextView: TextView = itemView.findViewById(R.id.chance_of_precip)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyCardsViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.hourly_card_home, parent, false)
        return HourlyCardsViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: HourlyCardsViewHolder, position: Int) {
        val hourlyWeather = hourlyWeatherList[position]
        var chance = hourlyWeather.chanceOfRain + hourlyWeather.chanceOfSnow
        if (chance > 100) chance = 100

        // Bind the views with the corresponding data
        holder.hourTextView.text = hourlyWeatherMapper.extractDateAndTime(hourlyWeather.date).second.substring(0, 2)
        holder.tempTextView.text = "${hourlyWeather.temperature}°C"
        holder.conditionTextView.text = hourlyWeather.description
        holder.iconImageView.setImageResource(hourlyWeather.icResId)
        holder.chanceTextView.text = "${chance}%"
    }

    override fun getItemCount(): Int {
        return hourlyWeatherList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newHourlyCardsList: List<HourlyWeather>) {
        hourlyWeatherList = newHourlyCardsList
        notifyDataSetChanged()
    }
}