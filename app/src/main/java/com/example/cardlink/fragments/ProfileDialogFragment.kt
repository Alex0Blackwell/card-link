package com.example.cardlink.fragments

/**
 * Fragment class used to modify the user's social media links
 * Empty fields will result in a platform not showing up on the user's profile card
 */
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.example.cardlink.R

class ProfileDialogFragment: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        var builder = AlertDialog.Builder(requireActivity())

        // inflate view
        var view: View =
            requireActivity().layoutInflater.inflate(R.layout.profile_dialogfragment, null)
        builder.setView(view)

        var dialog: Dialog
        // Handlers for when user selects "ok" in a dialog
        builder.setPositiveButton("ok") { _: DialogInterface, _: Int ->
            // save profile into ViewModel of parent activity
        }
        builder.setNegativeButton("cancel") { _: DialogInterface, _: Int ->
            dismiss()
        }

        dialog=builder.create()
        return dialog
    }
}