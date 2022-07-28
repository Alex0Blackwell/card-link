package com.example.cardlink.fragments

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ScanMode
import com.example.cardlink.R

private const val CAMERA_REQUEST_CODE = 101

class ScanningFragment : Fragment() {

    private lateinit var mQrScanner:CodeScanner
    private lateinit var mContext: Context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scanning, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mContext = view.context
        qrScannerSetUp(view)

    }

    private fun qrScannerSetUp(view: View){
        val qrScanner = view.findViewById<CodeScannerView>(R.id.qrScanner)
        mQrScanner = CodeScanner(mContext, qrScanner)

        mQrScanner.apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS

            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.CONTINUOUS
            isAutoFocusEnabled = true
            isFlashEnabled = false

//            decodeCallback = DecodeCallback {}

            qrScanner.setOnClickListener {
                mQrScanner.startPreview()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mQrScanner.startPreview()
    }

    override fun onPause() {
        mQrScanner.releaseResources()
        super.onPause()
    }


}