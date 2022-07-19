package com.example.cardlink

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.cardlink.R
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.AuthUI.IdpConfig
import com.firebase.ui.auth.AuthUI.IdpConfig.EmailBuilder
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.firebase.ui.auth.util.ExtraConstants
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth

class FirebaseUIActivity : AppCompatActivity() {
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button
    // See: https://developer.android.com/training/basics/intents/result
    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firebase_ui)
        println("debug: in FirebaseUIActivity")
        loginButton = findViewById(R.id.login_button)
        registerButton = findViewById(R.id.register_button)

        loginButton.setOnClickListener { _ ->
            createSignInIntent()
        }

        registerButton.setOnClickListener { _ ->

        }
    }

    private fun createSignInIntent() {

        // Choose authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            //AuthUI.IdpConfig.PhoneBuilder().build(),
            //AuthUI.IdpConfig.GoogleBuilder().build(),
            //AuthUI.IdpConfig.FacebookBuilder().build(),
            //AuthUI.IdpConfig.TwitterBuilder().build()
        )

        // Create and launch sign-in intent
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
        signInLauncher.launch(signInIntent)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser
            // ...
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...

        }
    }


    private fun signOut() {

        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                // ...
            }
    }

    private fun delete() {
        AuthUI.getInstance()
            .delete(this)
            .addOnCompleteListener {
                // ...
            }
    }

    private fun themeAndLogo() {
        val providers = emptyList<AuthUI.IdpConfig>()

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            //.setLogo(R.drawable.my_great_logo) // Set logo drawable
            //.setTheme(R.style.MySuperAppTheme) // Set theme
            .build()
        signInLauncher.launch(signInIntent)
    }

    private fun privacyAndTerms() {
        val providers = emptyList<AuthUI.IdpConfig>()
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setTosAndPrivacyPolicyUrls(
                "https://example.com/terms.html",
                "https://example.com/privacy.html")
            .build()
        signInLauncher.launch(signInIntent)
    }

    open fun emailLink() {
        val actionCodeSettings = ActionCodeSettings.newBuilder()
            .setAndroidPackageName( /* yourPackageName= */
                "...",  /* installIfNotAvailable= */
                true,  /* minimumVersion= */
                null)
            .setHandleCodeInApp(true) // This must be set to true
            .setUrl("https://google.com") // This URL needs to be whitelisted
            .build()

        val providers = listOf(
            EmailBuilder()
                .enableEmailLinkSignIn()
                .setActionCodeSettings(actionCodeSettings)
                .build()
        )
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
        signInLauncher.launch(signInIntent)
    }

    open fun catchEmailLink() {
        val providers: List<IdpConfig> = emptyList()

        if (AuthUI.canHandleIntent(intent)) {
            val extras = intent.extras ?: return
            val link = extras.getString("email_link_sign_in")
            if (link != null) {
                val signInIntent = AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setEmailLink(link)
                    .setAvailableProviders(providers)
                    .build()
                signInLauncher.launch(signInIntent)
            }
        }
    }
}