package com.example.cardlink.activities

//import com.example.cardlink.viewModels.ProfileViewModel
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.cardlink.R
import com.example.cardlink.Util
import com.example.cardlink.adapters.TabPageAdapter
import com.example.cardlink.viewModels.MainViewModel
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
    private lateinit var mainViewModel: MainViewModel
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private var created = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main)
        tabBarSetUp()
        if (savedInstanceState != null) {
            created = savedInstanceState.getInt("created", 0)
        }
        val isAuth = checkCurrentUser()
        if (isAuth) {
            println("debug: authenticated")
            mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
            database = Firebase.database.reference
            auth = Firebase.auth
            val user = auth.currentUser
            val userId = user?.uid
            if (userId != null && created != 1) {
                val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
                progressBar.visibility = View.VISIBLE;
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
                        println("warn: No previous profile image saved!")
                    }

                    mainViewModel.updateMyConnectionsViewModel()
                    mainViewModel.updateMyPinnedConnectionsViewModel()

                    // Retrieve user's profile information based on uuid
                    database.child("users").child(userId).get().addOnSuccessListener {
                        println("debug: entire entry ${it.value}")

                        mainViewModel.liveName.postValue(Util.asString(it.child("name").value))
                        mainViewModel.liveDescription.postValue(Util.asString(it.child("description").value))
                        mainViewModel.livePhone.postValue(Util.asString(it.child("phoneNumber").value))
                        mainViewModel.liveEmail.postValue(Util.asString(it.child("email").value))
                        mainViewModel.liveOccupation.postValue(Util.asString(it.child("occupation").value))

                        mainViewModel.liveLinkedIn.postValue(Util.asString(it.child("linkedin").value))
                        mainViewModel.liveGithub.postValue(Util.asString(it.child("github").value))
                        mainViewModel.liveTwitter.postValue(Util.asString(it.child("twitter").value))
                        mainViewModel.liveFacebook.postValue(Util.asString(it.child("facebook").value))
                        mainViewModel.liveWebsite.postValue(Util.asString(it.child("website").value))

                        // Extract fields from entry
                        mainViewModel.name = Util.asString(it.child("name").value)
                        mainViewModel.description = Util.asString(it.child("description").value)
                        mainViewModel.phone = Util.asString(it.child("phoneNumber").value)
                        mainViewModel.email = Util.asString(it.child("email").value)
                        mainViewModel.occupation = Util.asString(it.child("occupation").value)

                        mainViewModel.linkedin = Util.asString(it.child("linkedin").value)
                        mainViewModel.github = Util.asString(it.child("github").value)
                        mainViewModel.twitter = Util.asString(it.child("twitter").value)
                        mainViewModel.facebook = Util.asString(it.child("facebook").value)
                        mainViewModel.website = Util.asString(it.child("website").value)

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

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putInt("created", 1)
    }
}



