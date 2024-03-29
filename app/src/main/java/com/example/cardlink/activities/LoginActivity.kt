package com.example.cardlink.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cardlink.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

// Reference: https://github.com/firebase/snippets-android/blob/f29858162c455292d3d18c1cc31d6776b299acbd/auth/app/src/main/java/com/google/firebase/quickstart/auth/kotlin/EmailPasswordActivity.kt
class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button
    private lateinit var database: DatabaseReference

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth

        emailEditText = findViewById(R.id.login_email)
        passwordEditText = findViewById(R.id.login_password)
        loginButton = findViewById(R.id.login_button)
        registerButton = findViewById(R.id.register_button)

        loginButton.setOnClickListener { _ ->
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            signIn(email, password)
        }

        registerButton.setOnClickListener { _ ->
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            createAccount(email, password)
        }
    }

    private fun createAccount(email: String, password: String) {
        println("debug: attempting to create account user: $email password: $password")
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    println("debug: createUserWithEmail:success")
                    val user = auth.currentUser
                    val userId = user?.uid
                    if (userId != null) {
                        println("debug: uid = $userId")

                        // Initialize database
                        database = Firebase.database.reference

                        // Create new user entry in db path "users"
                        // Using userId as user key
                        val newUser = database.child("users").child(userId)

                        // Add email to user entry
                        newUser.child("email").setValue(email)
                        newUser.child("name").setValue("")
                        newUser.child("description").setValue("")
                        newUser.child("phoneNumber").setValue("")
                        newUser.child("occupation").setValue("")
                        newUser.child("linkedin").setValue("")
                        newUser.child("github").setValue("")
                        newUser.child("facebook").setValue("")
                        newUser.child("twitter").setValue("")
                        newUser.child("website").setValue("")
                    }

                    finish()

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "debug: createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun signIn(email: String, password: String) {
        println("debug: attempting to login user: $email password: $password")
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    println("debug: signInWithEmail:success")
                    finish()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                    println("debug: signInWithEmail:failure")
                }
            }
    }

    private fun sendEmailVerification() {

        val user = auth.currentUser!!
        user.sendEmailVerification()
            .addOnCompleteListener(this) { task ->
                // Email Verification sent
            }
    }

    companion object {
        private const val TAG = "EmailPassword"
    }
}