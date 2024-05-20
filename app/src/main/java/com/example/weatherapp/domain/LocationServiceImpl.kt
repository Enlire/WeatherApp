package com.example.weatherapp.domain

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.example.weatherapp.LocationPermissionNotGrantedException
import com.example.weatherapp.data.models.WeatherLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import java.util.Locale
import kotlin.math.abs

const val LOCATION_PERMISSION_REQUEST_CODE = 123
const val USE_DEVICE_LOCATION = "USE_DEVICE_LOCATION"
const val USER_LOCATION = "USER_LOCATION"

class LocationServiceImpl(
    context: Context
) : LocationService {
    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private val context = context.applicationContext
    private val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    override suspend fun getPreferredLocationName(): String {
        if (isUsingDeviceLocation()) {
            try {
                val deviceLocation = getLastDeviceLocation().await()
                    ?: return "${getCustomLocationName()}"
                return "${getLocationNameFromCoordinates(deviceLocation.latitude, deviceLocation.longitude)}"
            } catch (e: LocationPermissionNotGrantedException) {
                return "${getCustomLocationName()}"
            }
        }
        else
            return "${getCustomLocationName()}"
    }

    override suspend fun getPreferredLocationCoordinates(): Pair<Double, Double> {
        if (isUsingDeviceLocation()) {
            try {
                val deviceLocation = getLastDeviceLocation().await()
                    ?: return Pair(0.0, 0.0)
                return Pair(deviceLocation.latitude, deviceLocation.longitude)
            } catch (e: LocationPermissionNotGrantedException) {
                return Pair(0.0, 0.0)
            }
        }
        else
            return getCoordinatesFromAddress(getCustomLocationName())
    }

    override suspend fun hasLocationChanged(lastWeatherLocation: WeatherLocation): Boolean {
        val deviceLocationChanged = try {
            hasDeviceLocationChanged(lastWeatherLocation)
        } catch (e: LocationPermissionNotGrantedException) {
            false
        }
        return deviceLocationChanged || hasCustomLocationChanged(lastWeatherLocation)
    }

    override fun isLocationServiceEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    @SuppressLint("MissingPermission")
    private fun getLastDeviceLocation(): Deferred<Location?> {
        return if (hasLocationPermission())
            fusedLocationClient.lastLocation.asDeferred()
        else
            throw LocationPermissionNotGrantedException()
    }

    private suspend fun hasDeviceLocationChanged(lastWeatherLocation: WeatherLocation): Boolean {
        if (!isUsingDeviceLocation())
            return false

        val deviceLocation = getLastDeviceLocation().await()
            ?: return false

        val comparisonThreshold = 0.1
        return abs(deviceLocation.latitude - lastWeatherLocation.lat) > comparisonThreshold &&
                abs(deviceLocation.longitude - lastWeatherLocation.lon) > comparisonThreshold
    }

    private fun hasCustomLocationChanged(lastWeatherLocation: WeatherLocation): Boolean {
        if (!isUsingDeviceLocation()) {
            val customLocationName = getCustomLocationName()?.trim()
            return !customLocationName.equals(lastWeatherLocation.name, ignoreCase = true)
        }
        return false
    }

    private fun isUsingDeviceLocation(): Boolean {
        return preferences.getBoolean(USE_DEVICE_LOCATION, true)
    }

    private fun getCustomLocationName(): String? {
        return preferences.getString(USER_LOCATION, null)
    }

    fun hasLocationPermission() : Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    fun requestLocationPermissions(context: Activity) {
        if (!hasLocationPermission()) {
            ActivityCompat.requestPermissions(
                context,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    fun getCoordinatesFromAddress(locationName: String?) : Pair<Double, Double> {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses: List<Address>? = locationName?.let { geocoder.getFromLocationName(it, 1) }
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

    private fun getLocationNameFromCoordinates (latitude: Double, longitude: Double) : String? {
        val geocoder = Geocoder(context, Locale("ru", "RU"))
        try {
            val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses?.isNotEmpty() == true) {
                val address = addresses[0]
                return address.locality
            }
        } catch (e: Exception) {
            preferences.getString("USER_LOCATION", null)
        }
        return preferences.getString("USER_LOCATION", null)
    }

    private fun <T> Task<T>.asDeferred(): Deferred<T> {
        val deferred = CompletableDeferred<T>()

        this.addOnSuccessListener { result ->
            deferred.complete(result)
        }

        this.addOnFailureListener { exception ->
            deferred.completeExceptionally(exception)
        }

        return deferred
    }
}