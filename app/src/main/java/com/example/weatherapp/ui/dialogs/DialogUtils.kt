package com.example.weatherapp.ui.dialogs

import androidx.fragment.app.FragmentManager

class DialogUtils {
    companion object {
        fun showNoInternetDialog(fragmentManager: FragmentManager) {
            val dialogFragment = NoInternetDialogFragment()
            dialogFragment.show(fragmentManager, "NoInternetDialog")
            dialogFragment.isCancelable = false
        }

        fun showLocationEnableDialog(fragmentManager: FragmentManager, fragmentId: Int) {
            val dialogFragment = LocationEnableDialogFragment(fragmentId)
            dialogFragment.show(fragmentManager, "LocationEnableDialog")
            dialogFragment.isCancelable = false
        }
    }
}
