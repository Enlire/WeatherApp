package com.example.weatherapp.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.preference.PreferenceManager
import com.example.weatherapp.R
import com.example.weatherapp.ui.SettingsFragment

class LocationEnableDialogFragment : DialogFragment() {
    private val sharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(requireContext())
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val title = "Определение местоположения отключено"
        val message = "Для автоматического определения местоположения необходимо включить геолокацию. Перейти к настройкам?"

        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Да") { _, _ ->
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                activity?.startActivity(intent)
                Toast.makeText(requireContext(), "Пожалуйста, выполните повторный запрос после включения геолокации", Toast.LENGTH_LONG).show()
            }
            .setNegativeButton("Отмена") { dialog, _ ->
                val settingsFragment = parentFragmentManager.findFragmentById(R.id.settings) as? SettingsFragment
                settingsFragment?.setLocationSwitchChecked(false)
                sharedPreferences.edit().putBoolean("USE_DEVICE_LOCATION", false).apply()
                Toast.makeText(requireContext(), "Пожалуйста, выполните повторный запрос", Toast.LENGTH_LONG).show()
            }
            .create()

        alertDialog.setOnShowListener {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(ContextCompat.getColor(requireContext(), R.color.customButtonColor))
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(ContextCompat.getColor(requireContext(), R.color.main_text))
        }

        return alertDialog
    }
}