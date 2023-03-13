package com.example.narddogsinventory

import android.content.Intent
import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputBinding
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.ButtonBarLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.firebase.auth.FirebaseAuth
import java.nio.BufferUnderflowException

class LoginActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        val email = intent.getStringExtra("email")
        val displayName = intent.getStringExtra("name")

        findViewById<TextView>(R.id.textView).text = email + "\n" + displayName

        findViewById<Button>(R.id.signOutButton).setOnClickListener{
            auth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
        }

    }

    private fun replaceFragment(fragment:Fragment){

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.bottomNav, fragment)
        fragmentTransaction.commit()
    }


}