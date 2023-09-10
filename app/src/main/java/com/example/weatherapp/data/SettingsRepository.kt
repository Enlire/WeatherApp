package com.example.weatherapp.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SettingsRepository(val context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("APP_PREFERENCES", Context.MODE_PRIVATE)

    fun getSavedDeviceLocation() : Triple<Double, Double, String?> {
        return Triple(sharedPreferences.getFloat("LATITUDE", 0.0f).toDouble(),
        sharedPreferences.getFloat("LONGITUDE", 0.0f).toDouble(),
        sharedPreferences.getString("DEVICE_LOCATION", null))
    }

    fun getSavedUserLocation() : String? {
        return sharedPreferences.getString("USER_LOCATION", null)
    }

    fun saveDeviceLocation(latitude: Double, longitude: Double, name: String?) {
        sharedPreferences.edit {
            putFloat("LATITUDE", latitude.toFloat())
            putFloat("LONGITUDE", longitude.toFloat())
            putString("DEVICE_LOCATION", name.toString())
        }
    }

    fun saveUserLocation(userLocation: String) {
        sharedPreferences.edit {
            putString("USER_LOCATION", userLocation)
        }
    }
}