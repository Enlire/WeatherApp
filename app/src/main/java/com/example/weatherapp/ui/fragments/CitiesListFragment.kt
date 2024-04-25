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
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.ui.adapters.CitiesListAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CitiesListFragment : Fragment() {
    private lateinit var backButton: ImageView
    private lateinit var addButton: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CitiesListAdapter
    private lateinit var citiesList: MutableList<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.cities_list, container, false)

        recyclerView = view.findViewById(R.id.citiesRecyclerView)
        backButton = view.findViewById(R.id.back_button)
        addButton = view.findViewById(R.id.add_button)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        citiesList = loadCitiesListFromSharedPreferences(requireContext()).toMutableList()
        checkListSize()

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = CitiesListAdapter(requireContext(), citiesList) { cityName ->
            context?.let { onItemClick(it, cityName) }
        }
        recyclerView.adapter = adapter

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                citiesList = loadCitiesListFromSharedPreferences(requireContext()).toMutableList()
                checkListSize()
            }
        })

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
                citiesList.add(cityName)
                saveCitiesListToSharedPreferences(requireContext(), citiesList)
                adapter.updateData(citiesList)
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Отмена") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun checkListSize() {
        if (citiesList.size >= 10) {
            addButton.visibility = View.GONE
        } else {
            addButton.visibility = View.VISIBLE
        }
    }

    private fun onItemClick(context: Context, cityName: String) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPreferences.edit().putString("USER_LOCATION", cityName).apply()
        val settingsFragment = parentFragmentManager.findFragmentById(R.id.settings) as? SettingsFragment
        settingsFragment?.setLocationSwitchChecked(false)
        sharedPreferences.edit().putBoolean("USE_DEVICE_LOCATION", false).apply()
        requireActivity().supportFragmentManager.popBackStack()
    }


    private fun saveCitiesListToSharedPreferences(context: Context, citiesList: List<String>) {
        val sharedPreferences = context.getSharedPreferences("CitiesList", Context.MODE_PRIVATE)
        sharedPreferences.edit().putStringSet("cities", HashSet(citiesList)).apply()
    }

    private fun loadCitiesListFromSharedPreferences(context: Context): List<String> {
        val sharedPreferences = context.getSharedPreferences("CitiesList", Context.MODE_PRIVATE)
        val citiesSet = sharedPreferences.getStringSet("cities", HashSet()) ?: HashSet()
        return citiesSet.toList()
    }
}
