package com.example.narddogsinventory

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.content.Intent
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider


private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var register : Button
    private lateinit var login : Button
    private lateinit var auth : FirebaseAuth
    private lateinit var googleSignInClient : GoogleSignInClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        register = findViewById(R.id.registerButton)
        login = findViewById(R.id.loginButton)

        auth =  FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("890851707023-u4igendqs168g29ift6gnkkb4plkeuur.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)  //instance of Google Sign in

        register.setOnClickListener {
            val intentR = Intent(this@MainActivity, RegisterActivity::class.java)
            startActivity(intentR)
            finish()
        }


        findViewById<Button>(R.id.loginButton).setOnClickListener{
            signInGoogle()   //when button is clicked, google sign in launches
        }




    }

    private fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent  //opens the google sign in client
        launcher.launch(signInIntent)

    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result ->
            if(result.resultCode == Activity.RESULT_OK){
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleResults(task)
            }


    }

    private fun handleResults(task: Task<GoogleSignInAccount>) {

        if(task.isSuccessful){

            val account : GoogleSignInAccount? = task.result
            if(account != null){
                updateUI(account)
            }

        }

        else{
            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
        }

    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener{

            if(it.isSuccessful){

                val intentL = Intent(this, LoginActivity::class.java)
                intentL.putExtra("email", account.email)
                intentL.putExtra("name", account.displayName)
                startActivity(intentL)



            }
            else{
                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()

            }
        }



    }

}