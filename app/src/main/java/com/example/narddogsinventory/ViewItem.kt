package com.example.narddogsinventory

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi

class ViewItem : AppCompatActivity() {

    private lateinit var itemBrand: TextView
    private  var currentItem : ActiveListing? = null
    private var currentUser : EntryUser? = null
    private lateinit var back : Button

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_item)

        currentUser = intent.getParcelableExtra("currentUser", EntryUser::class.java)
        currentItem = intent.getParcelableExtra("currentItem", ActiveListing::class.java)

        back = findViewById(R.id.buttonReturn)
        itemBrand = findViewById(R.id.tvItemBrand)

        itemBrand.text = currentItem?.brand.toString()

        back.setOnClickListener{

            val intent = Intent(this, ViewInventory::class.java)
            intent.putExtra("currentUser", currentUser)
            startActivity(intent)
        }
    }




}