package com.example.cardlink.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.cardlink.R
import com.example.cardlink.adapter.ContactAdapter
import com.example.cardlink.dataLayer.Person
import com.example.cardlink.dialog.BusinessCardDialog
import com.example.cardlink.viewModels.MainViewModel


class NetworkFragment : Fragment() {
    private lateinit var profileViewModel: MainViewModel
    private lateinit var searchView: EditText
    private  var filteredPersons =  ArrayList<Person>()
    private  var originalPersons = ArrayList<Person>()

    private lateinit var pinnedAdapter:ContactAdapter
    private lateinit var pinnedList: ArrayList<Person>
    private lateinit var myPinnedContactsListView: ListView



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_network, container, false)

        profileViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]



        setupObservers(view)

        searchView = view.findViewById(R.id.searchView)
        searchView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty()) {
                    handleSearch(s.toString())
                }
                else {
                    val myContactsListView = view?.findViewById<ListView>(R.id.list_of_contacts)
                    val contactAdapter = ContactAdapter(requireActivity(), originalPersons)
                    if (myContactsListView != null) {
                        myContactsListView.adapter = contactAdapter
                    }
                }
            }
        })

        return view
    }

    private fun handleSearch(queryString:String) {
        filteredPersons.clear()
            for (person in originalPersons) {
                if (person.name.lowercase().contains(queryString)) {
                    filteredPersons.add(person)
                }
            val myContactsListView = view?.findViewById<ListView>(R.id.list_of_contacts)
            val contactAdapter = ContactAdapter(requireActivity(), filteredPersons)
            if (myContactsListView != null) {
                myContactsListView.adapter = contactAdapter
            }
            myContactsListView?.setOnItemClickListener { _, item: View, _, _ ->
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
                bundleUpPersonInfo.putString(BusinessCardDialog.uidKey, contact.primaryKey)
                businessCardDialog.arguments = bundleUpPersonInfo
                businessCardDialog.show(requireActivity().supportFragmentManager, "cardlink")
            }
        }
    }
    private fun setupObservers(view: View) {
        profileViewModel.myPinnedConnections.observe(viewLifecycleOwner) {
            println("debug: pinned fires with $it")
            val myPinnedContactsListView = view.findViewById<ListView>(R.id.pinned_list_of_contacts)
            val contactAdapter = ContactAdapter(requireActivity(), it)
            myPinnedContactsListView.adapter = contactAdapter
            myPinnedContactsListView.setOnItemClickListener { _, item: View, _, _ ->
                val contact = item.tag as ContactAdapter.ViewHolder
                val businessCardDialog = BusinessCardDialog()
                val bundleUpPersonInfo = Bundle()
                bundleUpPersonInfo.putString(BusinessCardDialog.nameKey, contact.person.name)
                bundleUpPersonInfo.putString(
                    BusinessCardDialog.descriptionKey,
                    contact.person.description
                )
                bundleUpPersonInfo.putString(BusinessCardDialog.phoneKey, contact.person.phone)
                bundleUpPersonInfo.putString(BusinessCardDialog.emailKey, contact.person.email)
                bundleUpPersonInfo.putString(BusinessCardDialog.occupationKey, contact.person.occupation)
                bundleUpPersonInfo.putString(BusinessCardDialog.linkedInKey, contact.person.linkedIn)
                bundleUpPersonInfo.putString(BusinessCardDialog.githubKey, contact.person.github)
                bundleUpPersonInfo.putString(BusinessCardDialog.facebookKey, contact.person.facebook)
                bundleUpPersonInfo.putString(BusinessCardDialog.twitterKey, contact.person.twitter)
                bundleUpPersonInfo.putString(BusinessCardDialog.websiteKey, contact.person.website)
                bundleUpPersonInfo.putString(BusinessCardDialog.uidKey, contact.primaryKey)
                bundleUpPersonInfo.putBoolean(BusinessCardDialog.pinKey, true)
                businessCardDialog.arguments = bundleUpPersonInfo
                businessCardDialog.show(requireActivity().supportFragmentManager, "cardlink")
            }
        }

        profileViewModel.myConnections.observe(viewLifecycleOwner) {
            val myContactsListView = view.findViewById<ListView>(R.id.list_of_contacts)

            originalPersons.clear()
            originalPersons.addAll(it)
            println("all connections: $it")

            if (searchView.text.toString() != "") {
                handleSearch(searchView.text.toString())
            }
            else {
                val contactAdapter = ContactAdapter(requireActivity(), it)
                myContactsListView.adapter = contactAdapter

                myContactsListView.setOnItemClickListener { _, item: View, _, _ ->
                    val contact = item.tag as ContactAdapter.ViewHolder
                    val businessCardDialog = BusinessCardDialog()
                    val bundleUpPersonInfo = Bundle()
                    bundleUpPersonInfo.putString(BusinessCardDialog.nameKey, contact.person.name)
                    bundleUpPersonInfo.putString(
                        BusinessCardDialog.descriptionKey,
                        contact.person.description
                    )
                    bundleUpPersonInfo.putString(BusinessCardDialog.phoneKey, contact.person.phone)
                    bundleUpPersonInfo.putString(BusinessCardDialog.emailKey, contact.person.email)
                    bundleUpPersonInfo.putString(BusinessCardDialog.occupationKey, contact.person.occupation)
                    bundleUpPersonInfo.putString(BusinessCardDialog.linkedInKey, contact.person.linkedIn)
                    bundleUpPersonInfo.putString(BusinessCardDialog.githubKey, contact.person.github)
                    bundleUpPersonInfo.putString(BusinessCardDialog.facebookKey, contact.person.facebook)
                    bundleUpPersonInfo.putString(BusinessCardDialog.twitterKey, contact.person.twitter)
                    bundleUpPersonInfo.putString(BusinessCardDialog.websiteKey, contact.person.website)
                    bundleUpPersonInfo.putString(BusinessCardDialog.uidKey, contact.primaryKey)
                    bundleUpPersonInfo.putBoolean(BusinessCardDialog.pinKey, false)
                    businessCardDialog.arguments = bundleUpPersonInfo
                    businessCardDialog.show(requireActivity().supportFragmentManager, "cardlink")
                }
            }
        }
    }
}