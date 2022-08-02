package com.example.cardlink.activities

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.cardlink.R
import com.example.cardlink.adapters.TabPageAdapter
import com.example.cardlink.viewModels.MainViewModel
import com.example.cardlink.viewModels.ProfileViewModel
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage


class MainActivity : AppCompatActivity() {
    private lateinit var viewPager:ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var profileImageViewModel: ProfileViewModel
    private lateinit var mainViewModel: MainViewModel
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tabBarSetUp()
        val isAuth = checkCurrentUser()
        if (isAuth) {
            println("debug: authenticated")
            mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
            database = Firebase.database.reference
            auth = Firebase.auth
            val user = auth.currentUser
            val userId = user?.uid
            if (userId != null) {
                val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
                progressBar.setVisibility(View.VISIBLE);
                // Download the user's profile picture
                Thread(Runnable {
                    val storageReference = FirebaseStorage.getInstance().reference
                    val photoReference = storageReference.child("images/${userId}/profile.jpg")
                    val ONE_MEGABYTE = (1024 * 1024 * 10).toLong()
                    photoReference.getBytes(ONE_MEGABYTE).addOnSuccessListener { bytes ->
                        val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        mainViewModel.userImage.value = bmp
                        println("Image finished downloading from Main!")
                    }.addOnFailureListener {
                        Toast.makeText(
                            this,
                            "No previous profile image saved!",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    // Retrieve user's profile information based on uuid
                    database.child("users").child(userId).get().addOnSuccessListener {
                        println("debug: entire entry ${it.value}")

                        // Extract fields from entry
                        mainViewModel.name = it.child("name").value as String
                        mainViewModel.description = it.child("description").value as String
                        mainViewModel.phone = it.child("phoneNumber").value as String
                        mainViewModel.email = it.child("email").value as String
                        mainViewModel.occupation = it.child("occupation").value as String

                        mainViewModel.linkedin = it.child("linkedin").value as String
                        mainViewModel.github = it.child("github").value as String
                        mainViewModel.twitter = it.child("twitter").value as String
                        mainViewModel.facebook = it.child("facebook").value as String
                        mainViewModel.website = it.child("website").value as String

                        println("Information finished downloading from Main!")
                        progressBar.setVisibility(View.GONE);
                    }.addOnFailureListener{
                        println("debug: firebase Error getting data $it")
                    }
                }).start()

            }


        } else {
            println("debug: not authenticated")
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun tabBarSetUp(){

        tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        viewPager = findViewById<ViewPager2>(R.id.viewPager)

        //setting up fragment adapter
        val adapter = TabPageAdapter(this, tabLayout.tabCount)
        viewPager.adapter = adapter

        //listens for user swipes on the tab layout
        viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                tabLayout.selectTab(tabLayout.getTabAt(position))

            }
        })

        //main activity view pager displays selected tab
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

        })

    }

    override fun onResume() {
        super.onResume()

    }

    private fun checkCurrentUser(): Boolean {
        val user = Firebase.auth.currentUser
        if (user != null) {
            // User is signed in
            println("debug: user $user signed in ")
            return true
        } else {
            // No user is signed in
            return false
        }
    }

    private fun getUserProfile() {
        val user = Firebase.auth.currentUser
        user?.let {
            // Name, email address, and profile photo Url
            val name = user.displayName
            val email = user.email
            val photoUrl = user.photoUrl

            // Check if user's email is verified
            val emailVerified = user.isEmailVerified

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            val uid = user.uid

            println("userinfo: $name $email")
        }
    }

    private fun getProviderData() {
        val user = Firebase.auth.currentUser
        user?.let {
            for (profile in it.providerData) {
                // Id of the provider (ex: google.com)
                val providerId = profile.providerId

                // UID specific to the provider
                val uid = profile.uid

                // Name, email address, and profile photo Url
                val name = profile.displayName
                val email = profile.email
                val photoUrl = profile.photoUrl
            }
        }
    }
}