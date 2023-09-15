package com.example.weatherapp.ui

import android.os.Bundle
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreference
import com.example.weatherapp.R
import com.example.weatherapp.data.SettingsRepository
import com.example.weatherapp.domain.LocationService

class SettingsFragment : PreferenceFragmentCompat() {
    private var locationSwitch: SwitchPreference? = null
    private var locationEditText: EditTextPreference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_settings, container, false)
//    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)

        val settingsRepository = SettingsRepository(requireContext())
        val locationService = LocationService(requireContext())
        locationEditText = findPreference<EditTextPreference>("USER_LOCATION")
        locationSwitch = findPreference<SwitchPreference>("USE_DEVICE_LOCATION")
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val hasLocationPermission = locationService.hasLocationPermission()

        locationSwitch?.isEnabled = hasLocationPermission
        locationEditText?.isEnabled = locationSwitch?.isChecked == false

        locationSwitch?.setOnPreferenceChangeListener { _, newValue ->
            val isChecked = newValue as Boolean
            if (isChecked) {
                locationService.startLocationUpdates()
                locationEditText?.isEnabled = false
                sharedPreferences.edit().putBoolean("switch_state", true).apply()
            } else {
                locationService.stopLocationUpdates()
                locationEditText?.isEnabled = true
                sharedPreferences.edit().putBoolean("switch_state", false).apply()
            }
            true
        }

        locationEditText?.setOnPreferenceChangeListener { _, newValue ->
            if (newValue is String) {
                settingsRepository.saveUserLocation(newValue)
                true
            } else {
                false
            }
        }
    }

    companion object {

    }
}