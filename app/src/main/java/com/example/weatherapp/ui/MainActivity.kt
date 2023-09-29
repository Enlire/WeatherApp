package com.example.weatherapp.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.domain.LocationService
import com.example.weatherapp.ui.fragments.CorrelationFragment
import com.example.weatherapp.ui.fragments.DailyWeatherFragment
import com.example.weatherapp.ui.fragments.HourlyWeatherFragment
import com.example.weatherapp.ui.fragments.MainFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private val sharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(MainFragment())

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

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.home -> replaceFragment(MainFragment())
                R.id.hourly -> replaceFragment(HourlyWeatherFragment())
                R.id.daily -> replaceFragment(DailyWeatherFragment())
                R.id.correlation -> replaceFragment(CorrelationFragment())
                R.id.settings -> replaceFragment(SettingsFragment())

                else -> {

                }
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }
}