package com.example.familysafety

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputBinding
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.initialize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.Executors

// web-client-id = 511136378026-t601h7035cgs12j6gtokhb8eutaqp5td.apps.googleusercontent.com
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: InputBinding
    private lateinit var auth: FirebaseAuth
    private var oneTapClient: SignInClient? = null
    private lateinit var signInRequest: BeginSignInRequest


    override fun onCreate(savedInstanceState: Bundle?) {
        FirebaseApp.initializeApp(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = Firebase.auth
        oneTapClient = Identity.getSignInClient(this)
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId(getString(R.string.web_client_id))
                    // Only show accounts previously used to sign in.
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()

    }

    fun signIn(view: View) {
        CoroutineScope(Dispatchers.Main).launch {
            signingGoogle()
        }
    }

    fun signOutUser(view: View){
        Firebase.auth.signOut()
        Toast.makeText(this, "Sign Out", Toast.LENGTH_LONG).show()
//        startActivity(Intent(this, LoginActivity::class.java))
//        binding.txtName.text = ""
//        binding.txtEmail.text = ""
//        binding.txtStatus.text = "Signed Out"
//        binding.imageView.setImageBitmap(null)
    }


    private suspend fun signingGoogle() {

        val result = oneTapClient?.beginSignIn(signInRequest)?.await()
        val intentSenderRequest = IntentSenderRequest.Builder(result!!.pendingIntent).build()
        activityResultLauncher.launch(intentSenderRequest)
    }

    private val activityResultLauncher: ActivityResultLauncher<IntentSenderRequest> =
        registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                try {
                    val credential = oneTapClient!!.getSignInCredentialFromIntent(result.data)
                    val idToken = credential.googleIdToken
                    if (idToken != null) {
                        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                        auth.signInWithCredential(firebaseCredential).addOnCompleteListener {
                            if (it.isSuccessful) {
//                                binding.progressBar.visibility = View.INVISIBLE
                                Toast.makeText(this, "Sign In Complete", Toast.LENGTH_LONG).show()
//                                showUser()
                                startActivity(Intent(this, MainActivity::class.java))
                            }
                        }
                    }
                } catch (e: ApiException) {
                    e.printStackTrace()
                }
            }
        }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        Log.d("currentUser", "onStart: $currentUser")
        if (currentUser != null) run {
//            startActivity(Intent(this, MainActivity::class.java))
            showUser()
        }
    }

    private fun showUser() {
        val user = Firebase.auth.currentUser
        user?.let {
            val name = it.displayName
            val email = it.email
            val photoUrl = it.photoUrl
            val emailVerified = it.isEmailVerified
//            binding.txtName.text = name
//            binding.txtEmail.text = email
            if (emailVerified) {
                Log.d("check", "showUser: ")
//                binding.txtStatus.text = "Verified Email"
            }
            var image: Bitmap? = null
            val imageURL = photoUrl.toString()
            val executorService = Executors.newSingleThreadExecutor()
            executorService.execute {
                try {
                    val `in` = java.net.URL(imageURL).openStream()
                    image = BitmapFactory.decodeStream(`in`)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            runOnUiThread {
                try {
                    Thread.sleep(1000)
//                    binding.imageView.setImageBitmap(image)
                } catch (e: InterruptedException){
                    e.printStackTrace()
            }
            }

        }
    }

}