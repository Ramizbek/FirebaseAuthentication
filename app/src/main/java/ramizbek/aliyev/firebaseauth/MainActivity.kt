package ramizbek.aliyev.firebaseauth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import ramizbek.aliyev.firebaseauth.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    //google signIn
    private lateinit var googleSignInClient: GoogleSignInClient

    //Show actions
    private val TAG = "MainActivity"

    //Key
    var RC_SIGN_IN = 1

    //Google Authentication
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //google sign in options = gso
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        //To authentication account
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        auth = FirebaseAuth.getInstance()

        binding.btnSign.setOnClickListener {
            Toast.makeText(this, "Click", Toast.LENGTH_SHORT).show()
            signIn()
        }
        binding.btnSign.setOnLongClickListener {
            auth.signOut()
            googleSignInClient.signOut().addOnCompleteListener(this,
                OnCompleteListener<Void?> { })
            Toast.makeText(this, "Left", Toast.LENGTH_SHORT).show()
            true
        }

    }

    //Only To Intent Account not add Account
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                Log.d(TAG, "onActivityResult: ${account.displayName}")
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.d(TAG, "onActivityResult: ")
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    // In this case i you can maybe just intent something instead of toast or etc...
                    Toast.makeText(this, "${auth.currentUser?.displayName}", Toast.LENGTH_SHORT)
                        .show()
                    Log.d(TAG, "firebaseAuthWithGoogle: ${auth.currentUser?.displayName}")
                } else {
                    Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "firebaseAuthWithGoogle: ${it.exception?.message}")
                }
            }
    }
}