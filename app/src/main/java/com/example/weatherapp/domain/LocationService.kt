package com.example.weatherapp.domain

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.example.weatherapp.data.SettingsRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Locale

class LocationService(private val context: Context) {

    private val geocoder = Geocoder(context, Locale.getDefault())
    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private val settingsRepository = SettingsRepository(context)
    private val LOCATION_PERMISSION_REQUEST_CODE = 123

    fun getLocation() : Triple<Double, Double, String> {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val isAutoLocationEnabled = sharedPreferences.getBoolean("USE_DEVICE_LOCATION", true)

        return if (isAutoLocationEnabled) {
            val deviceLocation = settingsRepository.getSavedDeviceLocation()
            Triple(deviceLocation.first, deviceLocation.second, deviceLocation.third.toString())
        } else {
            val userLocation = settingsRepository.getSavedUserLocation().toString()
            Log.d("loc5", userLocation)
            val userLocationCoordinates = getCoordinatesFromAddress(userLocation)
            Triple(userLocationCoordinates.first, userLocationCoordinates.second, userLocation)
        }
    }

    fun startLocationUpdates() {
        if (!isLocationServiceEnabled())
            return
        else
            getDeviceLocation()
    }

    fun stopLocationUpdates() {

    }

    @SuppressLint("MissingPermission")
    fun getDeviceLocation() {
        if (hasLocationPermission()) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    val deviceLocationName = getAddressFromCoordinates(latitude, longitude)
                    settingsRepository.saveDeviceLocation(latitude, longitude, deviceLocationName)
                } else {
                    // Местоположение не получено
                    // Возможно, вам нужно попробовать ещё раз или обработать этот случай
                }
            }
        }
    }

    private fun getAddressFromCoordinates (latitude: Double, longitude: Double) : String? {
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses?.isNotEmpty() == true) {
                val address = addresses[0]
                return address.locality
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun getCoordinatesFromAddress(locationName: String) : Pair<Double, Double> {
        try {
            val addresses: List<Address>? = geocoder.getFromLocationName(locationName, 1)
            if (addresses?.isNotEmpty() == true) {
                val latitude = addresses[0].latitude
                val longitude = addresses[0].longitude
                return Pair(latitude, longitude)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return Pair(0.0, 0.0)
    }

    fun isLocationServiceEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    fun hasLocationPermission() : Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    fun requestLocationPermissions() {
        if (!hasLocationPermission()) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }
}