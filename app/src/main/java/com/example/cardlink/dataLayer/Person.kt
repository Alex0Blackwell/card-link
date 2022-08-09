package com.example.cardlink.dataLayer

import android.graphics.Bitmap


data class Person(
    val primaryKey: String = "",
    val name: String = "",
    val description: String = "",
    val phone: String = "",
    val email: String = "",
    val occupation: String = "",
    val linkedIn: String = "",
    val github: String = "",
    val facebook: String = "",
    val twitter: String = "",
    val website: String = "",
    var profileImage: Bitmap? = null
)