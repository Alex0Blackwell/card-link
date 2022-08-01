package com.example.cardlink.viewModels

import android.graphics.Bitmap
import android.net.Uri
import android.text.Editable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class ProfileViewModel: ViewModel() {
    //userImage corresponds to the users profile image, saved as bitmap
    val userImage = MutableLiveData<Bitmap>()
    var database = Firebase.database.reference

    // Create new user entry in db path "users"
    // Using userId as user key
    var auth: FirebaseAuth = Firebase.auth
    private val user = auth.currentUser
    private val userId = user?.uid

    var linkedin = ""
    var github = ""
    var twitter = ""
    var website = ""
    var facebook = ""

    fun updateProfile(name: String, description: String?, phone: String?, email: String?, occupation: String?):Int{
        if (userId != null) {
                val currUser = database.child("users").child(userId)
                currUser.child("name").setValue(name)
                currUser.child("description").setValue(description)
                currUser.child("phoneNumber").setValue(phone)
                currUser.child("email").setValue(email)
                currUser.child("occupation").setValue(occupation)
                return 0
        }
        return -1
    }
    fun updateProfileLinks(linkedin: String, github: String, facebook: String, twitter:String, website:String):Int {
        println("updating profile links")
        if (userId != null) {
            val currUser = database.child("users").child(userId)
            currUser.child("linkedin").setValue(linkedin)
            currUser.child("github").setValue(github)
            currUser.child("facebook").setValue(facebook)
            currUser.child("twitter").setValue(twitter)
            currUser.child("website").setValue(website)
            return 0
        }
        return -1
    }

}