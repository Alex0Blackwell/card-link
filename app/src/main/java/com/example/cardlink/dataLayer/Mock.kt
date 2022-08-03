package com.example.cardlink.dataLayer

// TODO: This represents mock data. It's fine for now, but eventually we need to hook up Firebase.
// TODO: The Contact class needs to have its instance variables initialized with users from the firebase.
// https://stackoverflow.com/questions/61898858/firebase-realtime-database-listen-to-changes-with-specific-values
// This can be used to listen for updates pertaining to that user (for example, connections table with column of user1, user2, check if user1 or user2 contains uuid, if so,
// add it to the arrayadapter.

// Or this?: https://firebase.google.com/docs/database/android/lists-of-data
data class Contact(
    // unique identifier
    var uuid: String = "",

    var downloadURL:String = "", // the download url will be "images/${userId}/profile.jpg". There is no image url field in the database.

    // profile information
    var name: String = "" ,
    var description: String = "",
    var phone: String = "",
    var email: String = "",
    var occupation: String = "",


    // social links
    var linkedin: String = "",
    var github: String = "",
    var twitter: String = "",
    var website: String = "",
    var facebook: String = ""
)

data class MockContact(
    val primaryKey: Int,
    val name: String,
    val occupation: String,
    val bio: String,
    val github: String,
)

class Mock {
    companion object {
        val mockedContacts: ArrayList<MockContact> = arrayListOf(
            MockContact(0, "John Doe", "Batman", "Deep voice and stuff", "www.batman.com"),
            MockContact(1, "Jane Doe", "Wonder Woman", "kachow", "www.ww.com"),
            MockContact(2, "Mr. Mock", "Superman", "It's mocking time", "www.mock.com"),
        )
    }
}