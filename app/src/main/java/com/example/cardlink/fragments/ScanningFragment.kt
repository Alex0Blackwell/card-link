package com.example.cardlink.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.budiyev.android.codescanner.*
import com.example.cardlink.R
import com.example.cardlink.Util
import com.example.cardlink.dataLayer.Person
import com.example.cardlink.viewModels.MainViewModel
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


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

            val database = Firebase.database.reference
            try {
                val receiverUserRef = database.child("users").child(it.toString())
                receiverUserRef.get().addOnSuccessListener { receiverUser ->

                    requireActivity().runOnUiThread {
                        val contact = Person(
                            it.toString(),
                            Util.asString(receiverUser.child("name").value),
                            Util.asString(receiverUser.child("description").value),
                            Util.asString(receiverUser.child("phoneNumber").value),
                            Util.asString(receiverUser.child("email").value),
                            Util.asString(receiverUser.child("occupation").value),
                            Util.asString(receiverUser.child("linkedin").value),
                            Util.asString(receiverUser.child("github").value),
                            Util.asString(receiverUser.child("facebook").value),
                            Util.asString(receiverUser.child("twitter").value)
                        )
                        showContactDialog(contact)
                    }
                }
            } catch (ex: Exception) {
                requireActivity().runOnUiThread {
                    showFailedDialog(it.toString())
                }
            }
        }
    }

    private fun showContactDialog(contact: Person) {
        val businessCardDialogView = View.inflate(context, R.layout.dialog_business_card, null)

        val dialog = AlertDialog.Builder(requireActivity())
            .setView(businessCardDialogView)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        dialog.findViewById<ImageView>(R.id.trashIcon)?.visibility = View.INVISIBLE
        dialog.findViewById<TextView>(R.id.network_card_new_contact)?.visibility = View.VISIBLE
        dialog.findViewById<TextView>(R.id.network_card_name)?.text = contact.name
        dialog.findViewById<TextView>(R.id.network_card_occupation)?.text = contact.occupation
        dialog.findViewById<TextView>(R.id.network_card_description)?.text = contact.description
        dialog.findViewById<TextView>(R.id.network_card_phone)?.text = contact.phone
        dialog.findViewById<TextView>(R.id.network_card_email)?.text = contact.email

        dialog.setOnDismissListener {
            mQrScanner.apply {
                scanMode = ScanMode.CONTINUOUS
            }
        }
    }

    private fun showFailedDialog(data: String) {
        val businessCardDialogView = View.inflate(context, R.layout.dialog_business_card_failed, null)

        val dialog = AlertDialog.Builder(requireActivity())
            .setView(businessCardDialogView)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        dialog.findViewById<TextView>(R.id.network_card_failed_data)?.text = data

        dialog.setOnDismissListener {
            mQrScanner.apply {
                scanMode = ScanMode.CONTINUOUS
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