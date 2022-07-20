package com.example.cardlink.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cardlink.R
import android.widget.ListView
import com.example.cardlink.adapter.ContactAdapter
import com.example.cardlink.dataLayer.Mock.Companion.mockedContacts
import com.example.cardlink.dialog.BusinessCardDialog


class NetworkFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_network, container, false)
        val myContactsListView = view.findViewById<ListView>(R.id.list_of_contacts)

        val contactAdapter = ContactAdapter(requireActivity(), mockedContacts)

        myContactsListView.adapter = contactAdapter

        myContactsListView.setOnItemClickListener { _, item: View, _, _ ->
            val contact = item.tag as ContactAdapter.ViewHolder
            val businessCardDialog = BusinessCardDialog()
            val bundleUpPrimaryKey = Bundle()
            bundleUpPrimaryKey.putInt(BusinessCardDialog.contactPrimaryKeyIdentifier, contact.primaryKey)
            businessCardDialog.arguments = bundleUpPrimaryKey
            businessCardDialog.show(requireActivity().supportFragmentManager, "cardlink")
        }

        return view
    }
}