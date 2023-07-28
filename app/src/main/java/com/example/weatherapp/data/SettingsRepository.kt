package com.example.weatherapp.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SettingsRepository(val context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("APP_PREFERENCES", Context.MODE_PRIVATE)
    private val editor = sharedPreferences.edit()

    fun getSavedUserLocation(): String? {
        return sharedPreferences.getString("USER_LOCATION", null)
    }

    fun saveUserLocation(userLocation: String) {
        sharedPreferences.edit {
            putString("USER_LOCATION", userLocation)
        }
    }
}