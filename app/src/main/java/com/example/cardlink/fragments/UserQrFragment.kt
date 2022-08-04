package com.example.cardlink.fragments

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cardlink.R
import com.example.cardlink.viewModels.MainViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
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

        println("debug: name -> ${profileViewModel.name}")
        if(userId != null) {
            println("debug: displaying user ID ($userId) on QR-code")
            val myBitmap: Bitmap = QRCode.from(userId).withSize(800, 800).bitmap()
            val myImage: ImageView = view.findViewById(R.id.qrImg) as ImageView
            myImage.setImageBitmap(myBitmap)
        }

        setupObservers(view)
    }

    private fun setupObservers(view: View) {
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
}