package com.example.weatherapp.ui.dialogs

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import com.example.weatherapp.R
import com.example.weatherapp.ui.SettingsFragment
import com.example.weatherapp.ui.fragments.MainFragment

class LocationEnableDialogFragment(private val currentFragmentId: Int) : DialogFragment() {
    private val sharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(requireContext())
    }
    private lateinit var locationSettingsLauncher: ActivityResultLauncher<Intent>

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val title = "Определение местоположения отключено"
        val message =
            "Для автоматического определения местоположения необходимо включить геолокацию. Перейти к настройкам?"

        locationSettingsLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            Toast.makeText(requireContext(), "Доступ к геолокации отключен. Некоторые функции могут быть недоступны.", Toast.LENGTH_LONG).show()

            if (result.resultCode == Activity.RESULT_OK) {
                Toast.makeText(requireContext(), "Доступ к геолокации отключен. Некоторые функции могут быть недоступны.", Toast.LENGTH_LONG).show()

                requireActivity().findNavController(R.id.nav_host_fragment_activity_main)
                    .navigate(currentFragmentId)
            }
            else {Toast.makeText(requireContext(), "Доступ к геолокации отключен. Некоторые функции могут быть недоступны.", Toast.LENGTH_LONG).show()
            }
        }

        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Да") { _, _ ->
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                locationSettingsLauncher.launch(intent)
            }
            .setNegativeButton("Отмена") { dialog, _ ->
                val settingsFragment =
                    parentFragmentManager.findFragmentById(R.id.settings) as? SettingsFragment
                settingsFragment?.setLocationSwitchChecked(false)
                sharedPreferences.edit().putBoolean("USE_DEVICE_LOCATION", false).apply()
                requireActivity().findNavController(R.id.nav_host_fragment_activity_main).navigate(currentFragmentId)
            }
            .create()

        alertDialog.setOnShowListener {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                ?.setTextColor(ContextCompat.getColor(requireContext(), R.color.customButtonColor))
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                ?.setTextColor(ContextCompat.getColor(requireContext(), R.color.main_text))
        }

        return alertDialog
    }
}