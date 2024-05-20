package com.example.weatherapp.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.weatherapp.R

class NoInternetDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val title = getString(R.string.no_internet_title)
        val message = getString(R.string.no_internet_message)
        val buttonText = getString(R.string.no_internet_button)

        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(buttonText) { _, _ ->
                dismiss()
            }
            .create()

        alertDialog.setOnShowListener {
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(ContextCompat.getColor(requireContext(), R.color.customButtonColor))
        }

        return alertDialog
    }
}