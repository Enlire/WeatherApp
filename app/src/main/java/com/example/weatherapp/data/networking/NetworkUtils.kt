package com.example.weatherapp.data.networking

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

class NetworkUtils {
    companion object {
        fun isInternetAvailable(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
            return actNw.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        }
    }
}