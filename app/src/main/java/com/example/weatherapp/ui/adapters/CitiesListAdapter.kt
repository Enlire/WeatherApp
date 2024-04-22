package com.example.weatherapp.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.domain.models.DailyWeather

class CitiesListAdapter(
    context: Context,
    private var citiesList: MutableList<String>
) : RecyclerView.Adapter<CitiesListAdapter.CitiesListViewHolder>() {

    class CitiesListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cityTextView: TextView = itemView.findViewById(R.id.city_name)
        val deleteButton: ImageView = itemView.findViewById(R.id.delete_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitiesListViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.cities_list_item, parent, false)
        return CitiesListViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CitiesListViewHolder, position: Int) {
        val cityName = citiesList[position]
        holder.cityTextView.text = cityName
        holder.deleteButton.setOnClickListener {
            citiesList.removeAt(holder.adapterPosition)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return citiesList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newCitiesList: MutableList<String>) {
        citiesList = newCitiesList
        notifyDataSetChanged()
    }
}