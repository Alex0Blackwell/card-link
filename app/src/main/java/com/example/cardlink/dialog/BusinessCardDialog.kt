package com.example.cardlink.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.cardlink.R
import com.example.cardlink.Util.Companion.downloadUserImage
import com.example.cardlink.dataLayer.Mock
import com.example.cardlink.dataLayer.MockContact
import com.example.cardlink.dataLayer.Person

class BusinessCardDialog: DialogFragment(), DialogInterface.OnClickListener {

    companion object {
        const val nameKey= "name_key"
        const val descriptionKey = "description_key"
        const val phoneKey= "phone_key"
        const val emailKey = "email_key"
        const val occupationKey = "occupation_key"
        const val linkedInKey = "linkedIn_key"
        const val githubKey = "github_key"
        const val facebookKey = "facebook_key"
        const val twitterKey = "twitter_key"
        const val websiteKey = "website_key"
        const val uidKey = "uid_key"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val name = arguments?.getString(nameKey)
        val description = arguments?.getString(descriptionKey)
        val phone = arguments?.getString(phoneKey)
        val email = arguments?.getString(emailKey)
        val occupation = arguments?.getString(occupationKey)
        val linkedIn = arguments?.getString(linkedInKey)
        val github = arguments?.getString(githubKey)
        val facebook = arguments?.getString(facebookKey)
        val twitter = arguments?.getString(twitterKey)
        val website = arguments?.getString(websiteKey)
        val uid = arguments?.getString(uidKey)

        val businessCardDialogView = View.inflate(context, R.layout.dialog_business_card, null)

        val dialog = AlertDialog.Builder(requireActivity())
            .setView(businessCardDialogView)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        dialog.findViewById<TextView>(R.id.network_card_name)?.text = name
        dialog.findViewById<TextView>(R.id.network_card_occupation)?.text = occupation
        dialog.findViewById<TextView>(R.id.network_card_description)?.text = description
        dialog.findViewById<TextView>(R.id.network_card_phone)?.text = phone
        dialog.findViewById<TextView>(R.id.network_card_email)?.text = email
        dialog.findViewById<ImageView>(R.id.network_card_profile_image)
            ?.let {
                if (uid != null) {
                    downloadUserImage(uid, it)
                }
            }
        return dialog
    }

    override fun onClick(dialog: DialogInterface?, which: Int) { }

    /**
     * Mocked! This will eventually need to pull the actual data via primary key.
     */
    private fun getContact(contactPrimaryKey: Int?): MockContact {
        return Mock.mockedContacts[contactPrimaryKey!!]
    }
}