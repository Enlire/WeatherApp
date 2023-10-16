package com.example.weatherapp.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import com.example.weatherapp.R
import com.example.weatherapp.ui.SettingsFragment

class LocationEnableDialogFragment(private val currentFragmentId: Int) : DialogFragment() {
    private val sharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(requireContext())
    }
    private lateinit var locationSettingsLauncher: ActivityResultLauncher<Intent>
    constructor(): this(0)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val title = "Определение местоположения отключено"
        val message =
            "Для автоматического определения местоположения необходимо включить геолокацию. Перейти к настройкам?"

        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Да") { _, _ ->
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                locationSettingsLauncher.launch(intent)
                sharedPreferences.edit().putInt("currentFragmentId", currentFragmentId).apply()
            }
            .setNegativeButton("Отмена") { _, _ ->
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationSettingsLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}
    }
}