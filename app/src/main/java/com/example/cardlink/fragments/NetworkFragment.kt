package com.example.cardlink.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cardlink.R
import android.widget.ListView
import androidx.lifecycle.ViewModelProvider
import com.example.cardlink.adapter.ContactAdapter
import com.example.cardlink.dialog.BusinessCardDialog
import com.example.cardlink.viewModels.MainViewModel


class NetworkFragment : Fragment() {
    private lateinit var profileViewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_network, container, false)

        profileViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        setupObservers(view)

        return view
    }

    private fun setupObservers(view: View) {
        profileViewModel.myConnections.observe(viewLifecycleOwner) {
            val myContactsListView = view.findViewById<ListView>(R.id.list_of_contacts)

            val contactAdapter = ContactAdapter(requireActivity(), it)

            myContactsListView.adapter = contactAdapter

            myContactsListView.setOnItemClickListener { _, item: View, _, _ ->
                val contact = item.tag as ContactAdapter.ViewHolder
                val businessCardDialog = BusinessCardDialog()
                val bundleUpPersonInfo = Bundle()
                bundleUpPersonInfo.putString(BusinessCardDialog.nameKey, contact.person.name)
                bundleUpPersonInfo.putString(BusinessCardDialog.descriptionKey, contact.person.description)
                bundleUpPersonInfo.putString(BusinessCardDialog.phoneKey, contact.person.phone)
                bundleUpPersonInfo.putString(BusinessCardDialog.emailKey, contact.person.email)
                bundleUpPersonInfo.putString(BusinessCardDialog.occupationKey, contact.person.occupation)
                bundleUpPersonInfo.putString(BusinessCardDialog.linkedInKey, contact.person.linkedIn)
                bundleUpPersonInfo.putString(BusinessCardDialog.githubKey, contact.person.github)
                bundleUpPersonInfo.putString(BusinessCardDialog.facebookKey, contact.person.facebook)
                bundleUpPersonInfo.putString(BusinessCardDialog.twitterKey, contact.person.twitter)
                bundleUpPersonInfo.putString(BusinessCardDialog.websiteKey, contact.person.website)
                businessCardDialog.arguments = bundleUpPersonInfo
                businessCardDialog.show(requireActivity().supportFragmentManager, "cardlink")
            }
        }
    }
}