package dk.itu.moapd.scootersharing.mgan.activites

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.material.snackbar.Snackbar
import dk.itu.moapd.scootersharing.mgan.R

class LoginActivity : AppCompatActivity() {
    private val signInLauncher =
        registerForActivityResult(
            FirebaseAuthUIActivityResultContract()
        ) { result -> onSignInResult(result) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createSignInIntent()
    }

    private fun createSignInIntent() {
        // Choose authentication providers.
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build())
        // Create and launch sign-in intent.
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setIsSmartLockEnabled(false)
            .setAvailableProviders(providers)
            .setTheme(R.style.Theme_ScooterSharing)
            .build()
        signInLauncher.launch(signInIntent)
    }

    private fun onSignInResult(
        result: FirebaseAuthUIAuthenticationResult
    ) {
        if (result.resultCode == RESULT_OK) {
            Snackbar.make(
                findViewById(android.R.id.content),
                "User logged in the app.",
                Snackbar.LENGTH_SHORT
            ).show()
            startMainActivity()
        } else
            Snackbar.make(
                findViewById(android.R.id.content),
                "Authentication failed.",
                Snackbar.LENGTH_SHORT
            ).show()
    }

    private fun startMainActivity() {
        val intent = Intent(
            this,
            MainActivity::class.java
        )
        startActivity(intent)
        finish()
    }
}