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
import androidx.fragment.app.activityViewModels
import com.example.cardlink.interfaces.LinkContract
import com.example.cardlink.R
import com.example.cardlink.viewModels.MainViewModel
import com.google.android.material.textfield.TextInputEditText

class ProfileDialogFragment: DialogFragment() {
    private lateinit var linkedinText: TextInputEditText
    private lateinit var githubText: TextInputEditText
    private lateinit var facebookText: TextInputEditText
    private lateinit var twitterText: TextInputEditText
    private lateinit var websiteText: TextInputEditText

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        var builder = AlertDialog.Builder(requireActivity())
        // inflate view
        var view: View =
            requireActivity().layoutInflater.inflate(R.layout.profile_dialogfragment, null)
        builder.setView(view)



        linkedinText = view.findViewById(R.id.linkedinEdit)
        githubText = view.findViewById(R.id.githubEdit)
        facebookText = view.findViewById(R.id.fbEdit)
        twitterText = view.findViewById(R.id.twitterEdit)
        websiteText = view.findViewById(R.id.websiteEdit)

        // retrieves a shared view model instantiated in parent activity.
        val profileViewModel: MainViewModel by activityViewModels()

        linkedinText.setText(profileViewModel.linkedin)
        githubText.setText(profileViewModel.github)
        facebookText.setText(profileViewModel.facebook)
        twitterText.setText(profileViewModel.twitter)
        websiteText .setText(profileViewModel.website)

        var dialog: Dialog

        // Handlers for when user selects "ok" in a dialog.
        builder.setPositiveButton("Save") { _: DialogInterface, _: Int ->
            // Calls profileViewModel's (MainViewModel) updateProfileLinks function to update the record and also the fields in the ViewModel.
            Thread(Runnable {
                profileViewModel.updateProfileLinks(linkedinText.text.toString(),githubText.text.toString(),facebookText.text.toString(),twitterText.text.toString(),websiteText.text.toString())
            }).start()
            var mHost = getTargetFragment() as LinkContract

            // not sure if this is needed.
            mHost.methodToPassMyData(linkedinText.text.toString(),githubText.text.toString(),facebookText.text.toString(),twitterText.text.toString(),websiteText.text.toString())
        }
        builder.setNegativeButton("Cancel") { _: DialogInterface, _: Int ->
            dismiss()
        }
        dialog=builder.create()
        return dialog
    }

}