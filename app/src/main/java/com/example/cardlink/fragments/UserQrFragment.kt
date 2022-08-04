package com.example.cardlink.fragments

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.cardlink.R
import com.example.cardlink.adapters.TabPageAdapter
import com.example.cardlink.viewModels.MainViewModel
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView
import net.glxn.qrgen.android.QRCode


class UserQrFragment : Fragment() {
    lateinit var qrView: View
    lateinit var profileViewModel: MainViewModel
    var userId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // TODO: Add a viewmodel observer for username and occupation
        return inflater.inflate(R.layout.fragment_user_qr, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        qrView = view
        val user = Firebase.auth.currentUser
        userId = user?.uid
        profileViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        if(userId != null) {
            println("debug: displaying user ID ($userId) on QR-code")
            val myBitmap: Bitmap = QRCode.from(userId).withSize(800, 800).bitmap()
            val myImage: ImageView = view.findViewById(R.id.qrImg) as ImageView
            myImage.setImageBitmap(myBitmap)
        }

        setupObservers(view)
        makeCardClickable(view)
    }

    private fun setupObservers(view: View) {
        profileViewModel.userImage.observe(viewLifecycleOwner) {
            val viewToUpdate: CircleImageView = view.findViewById(R.id.my_card_profile_image)

            viewToUpdate.setImageBitmap(it)
        }
        profileViewModel.liveName.observe(viewLifecycleOwner) {
            val viewToUpdate: TextView = view.findViewById(R.id.my_card_name)
            viewToUpdate.text = it
        }
        profileViewModel.liveOccupation.observe(viewLifecycleOwner) {
            val viewToUpdate: TextView = view.findViewById(R.id.my_card_occupation)
            viewToUpdate.text = it
        }
        profileViewModel.liveDescription.observe(viewLifecycleOwner) {
            val viewToUpdate: TextView = view.findViewById(R.id.my_card_description)
            viewToUpdate.text = it
        }
        profileViewModel.livePhone.observe(viewLifecycleOwner) {
            val viewToUpdate: TextView = view.findViewById(R.id.my_card_phone)
            viewToUpdate.text = it
        }
        profileViewModel.liveEmail.observe(viewLifecycleOwner) {
            val viewToUpdate: TextView = view.findViewById(R.id.my_card_email)
            viewToUpdate.text = it
        }
    }

    /**
     * Switch to the profile tab to update your QR-code.
     */
    private fun makeCardClickable(view: View) {
        val cardView = view.findViewById<View>(R.id.my_card_container)
        cardView.setOnClickListener {
            val tabLayout = requireActivity().findViewById<TabLayout>(R.id.tabLayout)
            val viewPager = requireActivity().findViewById<ViewPager2>(R.id.viewPager)
            val adapter = TabPageAdapter(requireActivity(), tabLayout.tabCount)
            viewPager.adapter = adapter

            tabLayout.selectTab(tabLayout.getTabAt(3))
        }
    }
}