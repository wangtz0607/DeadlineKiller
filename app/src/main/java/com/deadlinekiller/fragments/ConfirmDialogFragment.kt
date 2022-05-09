package com.deadlinekiller.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class ConfirmDialogFragment(
    private val message: String,
    private val positiveButtonText: String,
    private val negativeButtonText: String,
    private val onPositiveButtonPressed: () -> Unit
) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setMessage(message)
            .setPositiveButton(positiveButtonText) { _, _ ->
                onPositiveButtonPressed()
                dismiss()
            }
            .setNegativeButton(negativeButtonText) { _, _ -> }
            .create()
}
