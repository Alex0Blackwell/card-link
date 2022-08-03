package com.example.cardlink.fragments

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.cardlink.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import net.glxn.qrgen.android.QRCode

class UserQrFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user_qr, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val user = Firebase.auth.currentUser
        val userId = user?.uid
        if(userId != null) {
            println("debug: displaying user ID ($userId) on QR-code")
            val myBitmap: Bitmap = QRCode.from(userId).bitmap()
            val myImage: ImageView = view.findViewById(R.id.qrImg) as ImageView
            myImage.setImageBitmap(myBitmap)
        }
    }
}