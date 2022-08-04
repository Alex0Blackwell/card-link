package com.example.cardlink.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
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
    private val maxScreenBrightness = 255
    private lateinit var qrView: View
    lateinit var profileViewModel: MainViewModel
    var oldScreenBrightness: Int = 100

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

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onResume() {
        super.onResume()
        rememberOldScreenBrightness()
        changeScreenBrightness(maxScreenBrightness)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onPause() {
        super.onPause()
        changeScreenBrightness(oldScreenBrightness)
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

    private fun rememberOldScreenBrightness() {
        oldScreenBrightness = Settings.System.getInt(
            requireActivity().contentResolver,
            Settings.System.SCREEN_BRIGHTNESS
        )
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun changeScreenBrightness(screenBrightnessValue: Int) {
        val settingsCanWrite = hasWriteSettingsPermission()

        if (!settingsCanWrite) {
            changeWriteSettingsPermission()
        } else {
            setScreenBrightness(screenBrightnessValue)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun hasWriteSettingsPermission(): Boolean {
        return Settings.System.canWrite(requireActivity())
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun changeWriteSettingsPermission() {
        Toast.makeText(
            requireActivity(),
            "Enable Cardlink permissions for dynamic screen brightness",
            Toast.LENGTH_LONG
        ).show()
        val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
        requireActivity().startActivity(intent)
    }

    private fun setScreenBrightness(screenBrightnessValue: Int) {
        Settings.System.putInt(
            requireActivity().contentResolver,
            Settings.System.SCREEN_BRIGHTNESS_MODE,
            Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
        )
        Settings.System.putInt(
            requireActivity().contentResolver,
            Settings.System.SCREEN_BRIGHTNESS,
            screenBrightnessValue
        )
    }
}