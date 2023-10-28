package com.example.weatherapp.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.domain.LocationService
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private val sharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        when (sharedPreferences.getString("APP_THEME", "light")) {
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

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val locationService = LocationService(this)
        locationService.requestLocationPermissions()
        if (!locationService.hasLocationPermission()) {
            val settingsFragment = supportFragmentManager.findFragmentById(R.id.settings) as? SettingsFragment
            settingsFragment?.setLocationSwitchEnabled(false)
            settingsFragment?.setLocationSwitchChecked(false)
            sharedPreferences.edit().putBoolean("USE_DEVICE_LOCATION", false).apply()
        }

        WindowCompat.setDecorFitsSystemWindows(
            window,
            false
        )

        val navView: BottomNavigationView = binding.bottomNavigationView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val callingFragmentId = sharedPreferences.getInt("currentFragmentId", -1)
        if (callingFragmentId != -1) {
            findNavController(R.id.nav_host_fragment_activity_main).navigate(callingFragmentId)
        }
    }
}