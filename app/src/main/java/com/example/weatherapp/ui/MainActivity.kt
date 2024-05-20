package com.example.weatherapp.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.domain.LOCATION_PERMISSION_REQUEST_CODE
import com.example.weatherapp.domain.LocationServiceImpl
import com.example.weatherapp.ui.dialogs.DialogUtils
import com.example.weatherapp.ui.fragments.SettingsFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import kotlin.math.abs


class MainActivity : AppCompatActivity(), KodeinAware {
    override val kodein by closestKodein()
    private val fusedLocationProviderClient: FusedLocationProviderClient by instance()

    private lateinit var locationManager: LifecycleBoundLocationManager
    private lateinit var binding : ActivityMainBinding
    private var downX: Int = 0
    private val sharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(this)
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
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

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!isInternetAvailable())
            DialogUtils.showNoInternetDialog(supportFragmentManager)

        val locationService = LocationServiceImpl(this)

        locationService.requestLocationPermissions(this)
        if (locationService.hasLocationPermission()) {
            bindLocationManager()
        }

        WindowCompat.setDecorFitsSystemWindows(
            window, false
        )

        val navView: BottomNavigationView = binding.bottomNavigationView
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHostFragment.navController
        val navGraph = navController.navInflater.inflate(R.navigation.mobile_navigation)
        navGraph.setStartDestination(R.id.home)
        navController.graph = navGraph
        navView.setupWithNavController(navController)
    }

    private fun bindLocationManager() {
        locationManager = LifecycleBoundLocationManager(
            this,
            fusedLocationProviderClient,
            locationCallback
        )
        lifecycle.addObserver(locationManager)
    }

    private fun isInternetAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        return actNw.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val callingFragmentId = sharedPreferences.getInt("currentFragmentId", -1)
        if (callingFragmentId != -1) {
            findNavController(R.id.nav_host_fragment_activity_main).navigate(callingFragmentId)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                bindLocationManager()
            else {
                val settingsFragment = supportFragmentManager.findFragmentById(R.id.settings) as? SettingsFragment
                settingsFragment?.setLocationSwitchEnabled(false)
                settingsFragment?.setLocationSwitchChecked(false)
                sharedPreferences.edit().putBoolean("USE_DEVICE_LOCATION", false).apply()
            }
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        if (event!!.action == MotionEvent.ACTION_DOWN) {
            downX = event.rawX.toInt()
        }

        if (event.action == MotionEvent.ACTION_UP) {
            val v = currentFocus
            if (v is EditText) {
                val x = event.rawX.toInt()
                val y = event.rawY.toInt()
                //Was it a scroll - If skip all
                if (abs(downX - x) > 5) {
                    return super.dispatchTouchEvent(event)
                }
                val reducePx = 25
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                //Bounding box is to big, reduce it just a little bit
                outRect.inset(reducePx, reducePx)
                if (!outRect.contains(x, y)) {
                    v.clearFocus()
                    var touchTargetIsEditText = false
                    //Check if another editText has been touched
                    for (vi in v.getRootView().touchables) {
                        if (vi is EditText) {
                            val clickedViewRect = Rect()
                            vi.getGlobalVisibleRect(clickedViewRect)
                            //Bounding box is to big, reduce it just a little bit
                            clickedViewRect.inset(reducePx, reducePx)
                            if (clickedViewRect.contains(x, y)) {
                                touchTargetIsEditText = true
                                break
                            }
                        }
                    }
                    if (!touchTargetIsEditText) {
                        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                    }
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }
}