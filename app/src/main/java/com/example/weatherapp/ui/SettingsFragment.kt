package com.example.weatherapp.ui

import android.os.Bundle
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import androidx.preference.SwitchPreferenceCompat
import com.example.weatherapp.R
import com.example.weatherapp.data.SettingsRepository
import com.example.weatherapp.domain.LocationService

class SettingsFragment : PreferenceFragmentCompat() {

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
        val locationEditText = findPreference<EditTextPreference>("USER_LOCATION")
        val locationSwitch = findPreference<SwitchPreference>("USE_DEVICE_LOCATION")

        locationSwitch?.setOnPreferenceChangeListener { _, newValue ->
            val isEnabled = newValue as Boolean
            if (isEnabled) {
                locationService.startLocationUpdates()
            } else {
                locationService.stopLocationUpdates()
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