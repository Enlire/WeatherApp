package com.example.weatherapp.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class APIErrorDialog(private val message: String) : DialogFragment() {
    constructor() : this("Ошибка при получении данных о погоде. Попробуйте повторить запрос.")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val title = "Что-то пошло не так"
        val alertDialog = AlertDialog.Builder(requireContext())
            .setMessage(message)
            .setTitle(title)
            .setPositiveButton("OK") { alertDialog, _ ->
                alertDialog.dismiss()
            }
            .create()

        return alertDialog
    }
}