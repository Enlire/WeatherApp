package com.example.weatherapp.domain

import android.content.Context
import android.location.Address
import android.location.Geocoder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.IOException
import java.util.*

class LocationService(private val context: Context) {

    private val geocoder = Geocoder(context, Locale.getDefault())

    fun getCoordinatesFromAddress(locationName: String): Pair<Double, Double>? {
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

        return null
    }
}