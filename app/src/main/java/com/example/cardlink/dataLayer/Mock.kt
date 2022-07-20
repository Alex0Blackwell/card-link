package com.example.cardlink.dataLayer

// TODO: This represents mock data. It's fine for now, but eventually we need to hook up Firebase.

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