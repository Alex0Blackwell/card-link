package com.example.cardlink.fragments

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.budiyev.android.codescanner.*
import com.example.cardlink.R
import com.example.cardlink.viewModels.MainViewModel
import com.google.zxing.Result


private const val CAMERA_REQUEST_CODE = 101

class ScanningFragment : Fragment() {
    private lateinit var profileViewModel: MainViewModel
    private lateinit var mQrScanner:CodeScanner
    private lateinit var mContext: Context
    private lateinit var dialog: AlertDialog

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

        //checks for camera permissions
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE);
        }

        qrScannerSetUp(view)
    }

    private fun qrScannerSetUp(view: View){
        profileViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        val qrScanner = view.findViewById<CodeScannerView>(R.id.qrScanner)
        mQrScanner = CodeScanner(mContext, qrScanner)

        //qr scanner initializations
        mQrScanner.apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS
            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.CONTINUOUS
            isAutoFocusEnabled = true
            isFlashEnabled = false

        }

        mQrScanner.decodeCallback = DecodeCallback {
            println("debug: got something: $it")

            //set to preview mode to no longer scan
            mQrScanner.apply {
                scanMode = ScanMode.PREVIEW
            }

            profileViewModel.addConnection(it.toString())

            requireActivity().runOnUiThread {
                val builder: AlertDialog.Builder? = activity?.let {
                    AlertDialog.Builder(it)
                }
                builder?.apply {
                    setMessage("Data: $it")
                    setTitle("Business Card")
                    setNegativeButton("Cancel"
                    ) { dialog, id ->


                    }
                }

                dialog = builder?.create()!!
                dialog.show()

                //until the dialog is dismissed users cannot scan a qr code
                dialog.setOnDismissListener {
                    mQrScanner.apply {
                        scanMode = ScanMode.CONTINUOUS
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        mQrScanner.startPreview()
        mQrScanner.apply {
            scanMode = ScanMode.CONTINUOUS
        }
    }

    override fun onPause() {
        mQrScanner.releaseResources()
        super.onPause()
    }

}