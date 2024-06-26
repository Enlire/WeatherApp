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

class DailyCardsAdapter(private var dailyWeatherList: List<DailyWeather>) :
    RecyclerView.Adapter<DailyCardsAdapter.DailyCardsViewHolder>() {

    class DailyCardsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dayTextView: TextView = itemView.findViewById(R.id.day)
        val tempTextView: TextView = itemView.findViewById(R.id.temp)
        val conditionTextView: TextView = itemView.findViewById(R.id.condition)
        val iconImageView: ImageView = itemView.findViewById(R.id.icon)
        val dateTextView: TextView = itemView.findViewById(R.id.date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyCardsViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.daily_card_home, parent, false)
        return DailyCardsViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DailyCardsViewHolder, position: Int) {
        val dailyWeather = dailyWeatherList[position]

        // Bind the views with the corresponding data
        holder.dayTextView.text = dailyWeather.day
        holder.tempTextView.text = "${dailyWeather.tempMin}°C / ${dailyWeather.tempMax}°C"
        holder.conditionTextView.text = dailyWeather.description
        holder.iconImageView.setImageResource(dailyWeather.icResId)
        holder.dateTextView.text = dailyWeather.date
    }

    override fun getItemCount(): Int {
        return dailyWeatherList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newDailyCardsList: List<DailyWeather>) {
        dailyWeatherList = newDailyCardsList
        notifyDataSetChanged()
    }
}