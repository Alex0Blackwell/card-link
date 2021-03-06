package com.example.cardlink.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.cardlink.R
import com.example.cardlink.dataLayer.Mock
import com.example.cardlink.dataLayer.MockContact

class BusinessCardDialog: DialogFragment(), DialogInterface.OnClickListener {

    companion object {
        const val contactPrimaryKeyIdentifier= "contact_primary_key"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val contactPrimaryKey = arguments?.getInt(contactPrimaryKeyIdentifier)
        // TODO: use the data layer to get a contact from its primary key.
        // Alternatively, we could store the whole contact object in a string with Gson.
         val contact = getContact(contactPrimaryKey)

        val businessCardDialogView = View.inflate(context, R.layout.dialog_business_card, null)

        val dialog = AlertDialog.Builder(requireActivity())
            .setView(businessCardDialogView)
            .setTitle("Business Card")
            .setNegativeButton("cancel",this)
            .create()
        dialog.show()

        dialog.findViewById<TextView>(R.id.business_card_dialog_name)?.text = contact.name
        dialog.findViewById<TextView>(R.id.business_card_dialog_occupation)?.text = contact.occupation
        dialog.findViewById<TextView>(R.id.business_card_dialog_bio)?.text = contact.bio
        dialog.findViewById<TextView>(R.id.business_card_dialog_github)?.text = contact.github
        // TODO: Set profile photo when we have real data

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