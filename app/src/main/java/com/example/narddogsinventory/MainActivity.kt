package com.example.narddogsinventory

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.content.Intent
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.ButtonBarLayout
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject


private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    //variables for quick reference to  widgets
    private lateinit var register : TextView
    private lateinit var login : Button
    private lateinit var auth : FirebaseAuth
    private lateinit var googleSignInClient : GoogleSignInClient
    private lateinit var db : FirebaseFirestore




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //assign button refs to xml widgets
        register = findViewById(R.id.tvRegister)
        login = findViewById(R.id.loginButton)

        //establish db access, and google SSO access
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        //Google SSO for signing in with gmail
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("890851707023-8olrib65tv0qstiae0b6sjr63suv5c8u.apps.googleusercontent.com")
            .requestEmail()
            .build()

        //access to Google sign in window
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        //moves to custom registration activity
        register.setOnClickListener {
            val intentR = Intent(this@MainActivity, RegisterActivity::class.java)
            startActivity(intentR)
            finish()
        }

        //using Google SSO. Will verify if new, or returning user
        findViewById<Button>(R.id.loginButton).setOnClickListener{

            signInGoogle()
        }

        //returning user sign in, Does not use Google SSO
        findViewById<Button>(R.id.credentialsButton).setOnClickListener{
            val credLogin = Intent(this, ValidateCredentials::class.java)
            startActivity(credLogin)
            finish()
        }

    }

    private fun signInGoogle() {

        //opens client window if Google account is new
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    //checks to see if gmail account is linked to android device
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result ->
            if(result.resultCode == Activity.RESULT_OK){
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleResults(task)
            }
    }

    //if access to Google established, and Gmail recognized access to app and/or registration
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

        //capture Google credentials for reference
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener{

            //credentials successful
            if(it.isSuccessful){

                //continue to dashboard, or redirect to register if new user
                newUser(account.email)
            }
            else{

                //unsuccessful credential validation
                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun newUser(email: String?) {

        //use Google email as value to to check if user document exists
        val emailCheck = db.collection("Users").document("$email")

        emailCheck.get().addOnCompleteListener{
            task ->
                if(task.isSuccessful){

                    //store document in variable for reference
                    val user = task.result
                    if (user.exists()){

                        //returning user, already registered. route to dashboard
                        //pass custom class object for local access to user data
                        Log.d("TAG", "User found")
                        val getUserID = user.toObject<EntryUser>()
                        val intentR = Intent(this@MainActivity, LoginActivity::class.java)
                        intentR.putExtra("currentUser", getUserID)
                        startActivity(intentR)
                        finish()

                    }
                    else{

                        //new user, route to registration with Gmail account as username
                        val intent = Intent(this, GoogleReg::class.java)
                        intent.putExtra("email", email)
                        startActivity(intent)
                        finish()
                    }
                }
                else{
                    //no access to db
                    Log.d("TAG", "User not found")
                }
        }
    }


}