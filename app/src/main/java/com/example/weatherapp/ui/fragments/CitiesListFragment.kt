package com.example.weatherapp.ui.fragments

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.view.marginStart
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.ui.adapters.CitiesListAdapter
import com.example.weatherapp.ui.adapters.HourlyCardsAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CitiesListFragment() : Fragment() {
    private lateinit var backButton: ImageView
    private lateinit var citiesList: MutableList<String>
    private lateinit var addButton: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CitiesListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.cities_list, container, false)

        recyclerView = view.findViewById(R.id.citiesRecyclerView)
        adapter = CitiesListAdapter(requireContext(), mutableListOf())
        backButton = view.findViewById(R.id.back_button)
        addButton = view.findViewById(R.id.add_button)
        citiesList = mutableListOf()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        citiesList = loadCitiesListFromSharedPreferences(requireContext()).toMutableList()
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter

        backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        addButton.setOnClickListener {
            showAddCityDialog(requireContext())
        }
    }

    private fun showAddCityDialog(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Введите название города")

        val editText = EditText(context)
        val container = FrameLayout(context)
        container.addView(editText)
        val containerParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        val marginHorizontal = 48F
        val marginTop = 16F
        containerParams.topMargin = (marginTop / 2).toInt()
        containerParams.leftMargin = marginHorizontal.toInt()
        containerParams.rightMargin = marginHorizontal.toInt()
        container.layoutParams = containerParams

        val superContainer = FrameLayout(context)
        superContainer.addView(container)
        builder.setView(superContainer)

        builder.setPositiveButton("OK") { dialog, _ ->
            val cityName = editText.text.toString()
            if (cityName.isNotBlank()) {
                addCity(cityName)
            } else {
                // Вывод сообщения об ошибке или некорректном вводе
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Отмена") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun saveCitiesListToSharedPreferences(context: Context, citiesList: List<String>) {
        val sharedPreferences = context.getSharedPreferences("CitiesList", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putStringSet("cities", HashSet(citiesList))
        editor.apply()
    }

    private fun loadCitiesListFromSharedPreferences(context: Context): List<String> {
        val sharedPreferences = context.getSharedPreferences("CitiesList", Context.MODE_PRIVATE)
        val citiesSet = sharedPreferences.getStringSet("cities", HashSet()) ?: HashSet()
        return ArrayList(citiesSet)
    }

    private fun addCity(cityName: String) {
        citiesList.add(cityName)
        adapter.updateData(citiesList)
        saveCitiesListToSharedPreferences(requireContext(), citiesList)
    }
}