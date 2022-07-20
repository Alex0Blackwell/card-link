package com.example.cardlink.fragments

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import com.example.cardlink.R
import net.glxn.qrgen.android.QRCode

class UserQrFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user_qr, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val myBitmap: Bitmap = QRCode.from("www.example.org").bitmap()
        val myImage: ImageView = view.findViewById(R.id.qrImg) as ImageView
        myImage.setImageBitmap(myBitmap)
    }
}