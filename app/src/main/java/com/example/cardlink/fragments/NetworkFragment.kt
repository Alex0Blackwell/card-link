package com.example.cardlink.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cardlink.R
import android.widget.ListView
import com.example.cardlink.adapter.ContactAdapter

data class MockContact(val name: String, val occupation: String)

class NetworkFragment : Fragment() {
    // TODO: Get actual contacts from viewModel
    private val mockedContacts: ArrayList<MockContact> = arrayListOf(
        MockContact("John Doe", "Batman"),
        MockContact("Jane Doe", "Wonder Woman"),
        MockContact("Mr. Mock", "Superman"),
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_network, container, false)
        val myContactsListView = view.findViewById<ListView>(R.id.list_of_contacts)

        val contactAdapter = ContactAdapter(requireActivity(), mockedContacts)

        myContactsListView.adapter = contactAdapter

        return view
    }
}