package com.example.cardlink.fragments

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.cardlink.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import net.glxn.qrgen.android.QRCode


class UserQrFragment : Fragment() {
    lateinit var qrView: View
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
        if(userId != null) {
            println("debug: displaying user ID ($userId) on QR-code")
            val myBitmap: Bitmap = QRCode.from(userId).withSize(800, 800).bitmap()
            val myImage: ImageView = view.findViewById(R.id.qrImg) as ImageView
            myImage.setImageBitmap(myBitmap)
        }
    }
}