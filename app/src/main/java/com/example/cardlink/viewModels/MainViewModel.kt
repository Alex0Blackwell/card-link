package com.example.cardlink.viewModels


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cardlink.Util
import com.example.cardlink.dataLayer.Person
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class MainViewModel: ViewModel() {
    //userImage corresponds to the users profile image, saved as bitmap
    val userImage = MutableLiveData<Bitmap>()
    var database = Firebase.database.reference

    // Create new user entry in db path "users"
    // Using userId as user key
    var auth: FirebaseAuth = Firebase.auth
    private val user = auth.currentUser
    private val userId = user?.uid

    val myConnections = MutableLiveData<ArrayList<Person>>()
    val myPinnedConnections = MutableLiveData<ArrayList<Person>>()

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



    fun addPin(receiverUserId: String) {
        // Note we have to embed all these promises because of async.
        if(userId != null) {
            try {
                val requesterUserRef = database.child("users").child(userId)
                val receiverUserRef = database.child("users").child(receiverUserId)

                requesterUserRef.get().addOnSuccessListener { requesterUser ->
                    receiverUserRef.get().addOnSuccessListener { receiverUser ->
                        if(requesterUser.exists() and receiverUser.exists()) {
                            val requesterConnectionRef = database.child("pinned").child(userId)
                            requesterConnectionRef.get().addOnSuccessListener {
                                if(idIsNew(receiverUserId, it)) {
                                    requesterConnectionRef.push().setValue(receiverUserId).addOnSuccessListener {
                                       println("pushed received into pinned!")
                                       updateMyPinnedConnectionsViewModel()
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (ex: Exception) {
                println("debug: getting the requester or receiver info failed")
            }
        }
    }

    fun removeContact(contactToRemove: String) {
        // Note we have to embed all these promises because of async.
        if(userId != null) {
            try {
                removePin(contactToRemove)
                var removed = false
                val requesterUserRef = database.child("users").child(userId)
                val contactToRemoveRef = database.child("users").child(contactToRemove)
                println("contact to remove: $userId")
                requesterUserRef.get().addOnSuccessListener { requesterUser ->
                    contactToRemoveRef.get().addOnSuccessListener { receiverUser ->
                        if(requesterUser.exists() and receiverUser.exists()) {
                            val requesterConnectionRef = database.child("connections").child(userId)
                            requesterConnectionRef.get().addOnSuccessListener {connections ->
                                for(contact in connections.children) {
                                    if (contact.value == contactToRemove) {
                                        contact.key?.let { database.child("connections").child(userId).child(
                                            contact.key!!
                                            ).removeValue().addOnSuccessListener {
                                                println("Removed from contact list!!")
                                                updateMyPinnedConnectionsViewModel()
                                                updateMyConnectionsViewModel()
                                                removed = true
                                             }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (ex: Exception) {
                println("debug: getting the requester or receiver info failed")
            }
        }
    }

    fun removePin(pinnedContactToRemove: String) {
        // Note we have to embed all these promises because of async.
        if(userId != null) {
            try {
                var removed = false
                val requesterUserRef = database.child("users").child(userId)
                val contactToRemoveRef = database.child("users").child(pinnedContactToRemove)

                requesterUserRef.get().addOnSuccessListener { requesterUser ->
                    contactToRemoveRef.get().addOnSuccessListener { receiverUser ->
                        if(requesterUser.exists() and receiverUser.exists()) {
                            val requesterConnectionRef = database.child("pinned").child(userId)
                            requesterConnectionRef.get().addOnSuccessListener {pinned ->
                                for(pinnedContact in pinned.children) {
                                    println("Pinned contact: $pinnedContact")
                                    println("Contact to remove: $pinnedContactToRemove")
                                    if (pinnedContact.value == pinnedContactToRemove) {
                                        pinnedContact.key?.let { database.child("pinned").child(userId).child(
                                            pinnedContact.key!!
                                        ).removeValue().addOnSuccessListener {
                                                println("Removed from pinned!!")
                                                updateMyPinnedConnectionsViewModel()
                                                removed = true
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (ex: Exception) {
                println("debug: getting the requester or receiver info failed")
            }
        }
    }



    fun addConnection(receiverUserId: String) {
        // Note we have to embed all these promises because of async.
        if(userId != null) {
            try {
                val requesterUserRef = database.child("users").child(userId)
                val receiverUserRef = database.child("users").child(receiverUserId)

                requesterUserRef.get().addOnSuccessListener { requesterUser ->
                    receiverUserRef.get().addOnSuccessListener { receiverUser ->
                        if(requesterUser.exists() and receiverUser.exists()) {
                            val requesterConnectionRef = database.child("connections").child(userId)
                            val receiverConnectionRef = database.child("connections").child(receiverUserId)

                            receiverConnectionRef.get().addOnSuccessListener {
                                if(idIsNew(userId, it))
                                    receiverConnectionRef.push().setValue(userId)
                            }
                            requesterConnectionRef.get().addOnSuccessListener {
                                if(idIsNew(receiverUserId, it)) {
                                    requesterConnectionRef.push().setValue(receiverUserId).addOnSuccessListener {
                                        updateMyConnectionsViewModel()
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (ex: Exception) {
                println("debug: getting the requester or receiver info failed")
            }
        }
    }

    private fun idIsNew(id: String, connections: DataSnapshot): Boolean {
        var isNew = true
        for(existingId in connections.children) {
            if(id == existingId.value)
                isNew = false
        }
        return isNew
    }

    fun updateMyPinnedConnectionsViewModel() {
        if(userId != null) {
            val myConnectionRef = database.child("pinned").child(userId)

            // Get all the connection IDs
            myConnectionRef.get().addOnSuccessListener { connectionIds ->
                myPinnedConnections.value = arrayListOf()
                val allConnections = arrayListOf<Person>()
                for(connectionId in connectionIds.children) {
                    println("connectionID: $connectionId")
                    val connectionRef = database.child("users").child(connectionId.value as String)
                    connectionRef.get().addOnSuccessListener {
                        val storageReference = FirebaseStorage.getInstance().reference
                        val photoReference = storageReference.child("images/${connectionId.value}/profile.jpg")
                        val ONE_MEGABYTE = (1024 * 1024 * 10).toLong()
                        var bmp: Bitmap? = null
                        val person = Person(
                            Util.asString(connectionId.value),
                            Util.asString(it.child("name").value),
                            Util.asString(it.child("description").value),
                            Util.asString(it.child("phoneNumber").value),
                            Util.asString(it.child("email").value),
                            Util.asString(it.child("occupation").value),
                            Util.asString(it.child("linkedin").value),
                            Util.asString(it.child("github").value),
                            Util.asString(it.child("facebook").value),
                            Util.asString(it.child("twitter").value)
                        )
                        photoReference.getBytes(ONE_MEGABYTE).addOnSuccessListener { bytes ->
                            println("getting bytes")
                            bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                            person.profileImage = bmp
                            println("bmp: $bmp")
                        }.addOnFailureListener {
                            println("pinned failed to get bytes, $it")
                        }
                        println("adding person to network list $person")
                        allConnections.add(person)
                        myPinnedConnections.postValue(allConnections)
                    }
                }
            }
        }
    }

    /**
     * Update the view model to have information on the user's connections so we don't have to hit
     * Firebase each time the network tab opens.
     */
    fun updateMyConnectionsViewModel() {
        if(userId != null) {
            val myConnectionRef = database.child("connections").child(userId)

            // Get all the connection IDs
            myConnectionRef.get().addOnSuccessListener { connectionIds ->
                myConnections.value = arrayListOf()
                val allConnections = arrayListOf<Person>()
                for(connectionId in connectionIds.children) {
                    println("connectionID: $connectionId")
                    val connectionRef = database.child("users").child(connectionId.value as String)
                    connectionRef.get().addOnSuccessListener {
                        val storageReference = FirebaseStorage.getInstance().reference
                        val photoReference = storageReference.child("images/${connectionId.value}/profile.jpg")
                        val ONE_MEGABYTE = (1024 * 1024 * 10).toLong()
                        var bmp: Bitmap? = null
                        val person = Person(
                            Util.asString(connectionId.value),
                            Util.asString(it.child("name").value),
                            Util.asString(it.child("description").value),
                            Util.asString(it.child("phoneNumber").value),
                            Util.asString(it.child("email").value),
                            Util.asString(it.child("occupation").value),
                            Util.asString(it.child("linkedin").value),
                            Util.asString(it.child("github").value),
                            Util.asString(it.child("facebook").value),
                            Util.asString(it.child("twitter").value)
                        )
                        photoReference.getBytes(ONE_MEGABYTE).addOnSuccessListener { bytes ->
                            println("getting bytes")
                            bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                            person.profileImage = bmp
                            println("bmp: $bmp")
                        }.addOnFailureListener {
                            println("failed to get bytes, $it")
                        }
                        println("adding person to network list $person")
                        allConnections.add(person)
                        myConnections.postValue(allConnections)
                    }
                }
            }
        }
    }

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