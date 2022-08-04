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

class MainViewModel: ViewModel() {
    //userImage corresponds to the users profile image, saved as bitmap
    val userImage = MutableLiveData<Bitmap>()
    var database = Firebase.database.reference

    // Create new user entry in db path "users"
    // Using userId as user key
    var auth: FirebaseAuth = Firebase.auth
    private val user = auth.currentUser
    private val userId = user?.uid

    val liveName = MutableLiveData<String>()
    val liveDescription = MutableLiveData<String>()
    val livePhone = MutableLiveData<String>()
    val liveEmail = MutableLiveData<String>()
    val liveOccupation = MutableLiveData<String>()
    val liveLinkedIn = MutableLiveData<String>()
    val liveGithub = MutableLiveData<String>()
    val liveTwitter = MutableLiveData<String>()
    val liveWebsite = MutableLiveData<String>()
    val liveFacebook= MutableLiveData<String>()

    var name = ""
    var description = ""
    var phone = ""
    var email = ""
    var occupation = ""

    var linkedin = ""
    var github = ""
    var twitter = ""
    var website = ""
    var facebook = ""

    fun updateProfile(name: String, description: String, phone: String, email: String, occupation: String):Int{
        if (userId != null) {
            val currUser = database.child("users").child(userId)
            currUser.child("name").setValue(name)
            currUser.child("description").setValue(description)
            currUser.child("phoneNumber").setValue(phone)
            currUser.child("email").setValue(email)
            currUser.child("occupation").setValue(occupation)

            // update viewmodel values
            liveName.postValue(name)
            liveDescription.postValue(description)
            livePhone.postValue(phone)
            liveEmail.postValue(email)
            liveOccupation.postValue(occupation)

            this.name = name
            this.description = description
            this.phone = phone
            this.email = email
            this.occupation = occupation

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

            liveLinkedIn.postValue(linkedin)
            liveGithub.postValue(github)
            liveTwitter.postValue(twitter)
            liveWebsite.postValue(website)
            liveFacebook.postValue(facebook)

            // update viewmodel values
            this.linkedin = linkedin
            this.github = github
            this.twitter = twitter
            this.website = website
            this.facebook = facebook

            return 0
        }
        return -1
    }

}