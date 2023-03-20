package com.example.narddogsinventory

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.firebase.auth.FirebaseAuth
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment




import android.view.MenuItem

import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarMenu
import com.google.android.material.navigation.NavigationBarView


class LoginActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth
    private lateinit var bNav: NavigationBarView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        auth = FirebaseAuth.getInstance()
        bNav = findViewById(R.id.bottomNav)

        val email = intent.getStringExtra("email")
        val displayName = intent.getStringExtra("name")

        findViewById<TextView>(R.id.textView).text = email + "\n" + displayName

        bNav.setOnItemSelectedListener   {
            when (it.itemId) {
                R.id.home -> {
                    val homeIntent= Intent(this, LoginActivity::class.java)
                    startActivity(homeIntent)
                    finish()
                    return@setOnItemSelectedListener true

                }
                R.id.AddItem -> {
                    val mainIntent= Intent(this, MainActivity::class.java)
                    startActivity(mainIntent)
                    return@setOnItemSelectedListener true

                }
                R.id.Inventory -> {
                    val inventoryIntent = Intent(this, ViewInventory::class.java)
                    startActivity(inventoryIntent)
                    return@setOnItemSelectedListener true
                }
                else ->{
                    return@setOnItemSelectedListener false
                }

            }


        }















        findViewById<Button>(R.id.signOutButton).setOnClickListener{
            auth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
    fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home -> {
                val homeIntent= Intent(ActivityA@this, LoginActivity::class.java)
                startActivity(homeIntent)

            }
            R.id.AddItem -> {
                val mainIntent= Intent(ActivityA@this, MainActivity::class.java)
                startActivity(mainIntent)

            }
            R.id.Inventory -> {
                val inventoryIntent = Intent(ActivityA@this, RegisterActivity::class.java)
                startActivity(inventoryIntent)
                return true
            }

        }
        return false
    }
}