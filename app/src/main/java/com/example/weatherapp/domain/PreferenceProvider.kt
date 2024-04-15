package com.example.weatherapp.domain

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

open class PreferenceProvider(context: Context) {
    private val appContext = context.applicationContext

    protected val preferences: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(appContext)
}
