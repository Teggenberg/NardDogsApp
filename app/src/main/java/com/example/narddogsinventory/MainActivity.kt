package com.example.narddogsinventory

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.content.Intent
import android.util.Log
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

    private lateinit var register : Button
    private lateinit var login : Button
    private lateinit var auth : FirebaseAuth
    private lateinit var googleSignInClient : GoogleSignInClient
    private lateinit var db : FirebaseFirestore



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        register = findViewById(R.id.registerButton)
        login = findViewById(R.id.loginButton)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("890851707023-8olrib65tv0qstiae0b6sjr63suv5c8u.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        register.setOnClickListener {
            val intentR = Intent(this@MainActivity, RegisterActivity::class.java)
            startActivity(intentR)
            finish()
        }


        findViewById<Button>(R.id.loginButton).setOnClickListener{



            signInGoogle()
        }

        findViewById<Button>(R.id.credentialsButton).setOnClickListener{
            val credLogin = Intent(this, ValidateCredentials::class.java)
            startActivity(credLogin)
            finish()
        }


    }

    private fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
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
                
                newUser(account.email)

                /*val intentL = Intent(this, LoginActivity::class.java)
                intentL.putExtra("email", account.email)
                intentL.putExtra("name", account.displayName)
                startActivity(intentL)*/

            }
            else{
                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun newUser(email: String?) {



        val emailCheck = db.collection("Users").document("$email")

        emailCheck.get().addOnCompleteListener{
            task ->
                if(task.isSuccessful){
                    val user = task.result
                    if (user.exists()){
                        Log.d("TAG", "User found")
                        //val getUserID = task.result.toObject(EntryUser::class.java)
                        val getUserID = user.toObject<EntryUser>()
                        val intentR = Intent(this@MainActivity, LoginActivity::class.java)
                        intentR.putExtra("email", email)
                        intentR.putExtra("name", getUserID?.firstName)

                        intentR.putExtra("currentUser", getUserID)
                        startActivity(intentR)
                        finish()

                    }
                    else{

                        val intent = Intent(this, GoogleReg::class.java)
                        startActivity(intent)
                        finish()


                    }
                }
                else{
                    Log.d("TAG", "User not found")


                }

        }

    }


}