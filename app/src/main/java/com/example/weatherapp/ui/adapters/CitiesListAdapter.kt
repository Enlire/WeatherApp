package com.example.weatherapp.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R

class CitiesListAdapter(
    context: Context,
    private var citiesList: MutableList<String>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<CitiesListAdapter.CitiesListViewHolder>() {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("CitiesList", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    init {
        loadCitiesListFromSharedPreferences(context)
    }

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
            citiesList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, citiesList.size)
            saveCitiesListToSharedPreferences(holder.itemView.context, citiesList)
            updateData(citiesList)
        }

        holder.itemView.setOnClickListener {
            onItemClick(cityName)
        }
    }

    private fun saveCitiesListToSharedPreferences(context: Context, citiesList: List<String>) {
        editor.putStringSet("cities", HashSet(citiesList))
        editor.apply()
    }

    private fun loadCitiesListFromSharedPreferences(context: Context) {
        citiesList = sharedPreferences.getStringSet("cities", HashSet())?.toMutableList() ?: mutableListOf()
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