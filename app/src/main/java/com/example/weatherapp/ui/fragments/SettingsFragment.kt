package com.example.weatherapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreference
import com.example.weatherapp.R
import com.example.weatherapp.data.SettingsRepository
import com.example.weatherapp.domain.LocationServiceImpl

class SettingsFragment : PreferenceFragmentCompat() {
    private var locationSwitch: SwitchPreference? = null
    private var locationEditText: EditTextPreference? = null
    private var themePreference: ListPreference? = null

    fun setLocationSwitchEnabled(isEnabled: Boolean) {
        locationSwitch = findPreference("USE_DEVICE_LOCATION")
        locationSwitch?.isEnabled = isEnabled
    }

    fun setLocationSwitchChecked(isChecked: Boolean) {
        locationSwitch = findPreference("USE_DEVICE_LOCATION")
        locationSwitch?.isChecked = isChecked
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)

        locationSwitch = findPreference("USE_DEVICE_LOCATION")
        locationEditText = findPreference("USER_LOCATION")
        themePreference = findPreference("APP_THEME")

        val settingsRepository = SettingsRepository(requireContext())
        val locationService = LocationServiceImpl(requireContext())
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val hasLocationPermission = locationService.hasLocationPermission()

        themePreference?.value = sharedPreferences.getString("APP_THEME", null)
        locationEditText?.summary = locationEditText?.text
        locationSwitch?.isEnabled = hasLocationPermission
        locationEditText?.isEnabled = locationSwitch?.isChecked == false

        locationSwitch?.setOnPreferenceChangeListener { _, newValue ->
            val isChecked = newValue as Boolean
            if (isChecked) {
                //locationService.startLocationUpdates()
                locationEditText?.isEnabled = false
                sharedPreferences.edit().putBoolean("USE_DEVICE_LOCATION", true).apply()
            } else {
                locationEditText?.isEnabled = true
                sharedPreferences.edit().putBoolean("USE_DEVICE_LOCATION", false).apply()
            }
            true
        }

        locationEditText?.setOnPreferenceChangeListener { _, newValue ->
            if (newValue is String) {
                settingsRepository.saveUserLocation(newValue)
                locationEditText?.summary = newValue
                true
            } else {
                false
            }
        }

        themePreference?.setOnPreferenceChangeListener { _, newValue ->
            val themeValue = newValue.toString()
            when (newValue.toString()) {
                "light" -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }

                "dark" -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }

                "system" -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                }
            }
            sharedPreferences.edit().putString("APP_THEME", themeValue).apply()
            sharedPreferences.edit().putBoolean("THEME_CHANGED", true).apply()
            true
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        view?.setBackgroundColor(resources.getColor(R.color.background));
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}