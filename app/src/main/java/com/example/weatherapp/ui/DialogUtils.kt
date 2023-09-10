package com.example.weatherapp.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.weatherapp.ui.dialogs.LocationEnableDialogFragment
import com.example.weatherapp.ui.dialogs.NoInternetDialogFragment

object DialogUtils {
    fun showNoInternetDialog(fragmentManager: FragmentManager) {
        val dialogFragment = NoInternetDialogFragment()
        dialogFragment.show(fragmentManager, "NoInternetDialog")
        dialogFragment.isCancelable = false
    }

    fun showLocationEnableDialog(fragmentManager: FragmentManager) {
        val dialogFragment = LocationEnableDialogFragment()
        dialogFragment.show(fragmentManager, "LocationEnableDialog")
        dialogFragment.isCancelable = false
    }
}
