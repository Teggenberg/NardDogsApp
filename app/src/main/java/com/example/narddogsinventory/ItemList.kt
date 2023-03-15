package com.example.narddogsinventory

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.core.content.ContentProviderCompat.requireContext

class ItemList : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*val coItems = arrayOf("Option1","Option2")
        val adapter = ArrayAdapter(this,R.layout.dropdown_text,coItems)
        val autoCompleteTextView = findViewById<AutoCompleteTextView>(R.id.etDropDownCon)
        autoCompleteTextView.setAdapter(adapter)*/


        setContentView(R.layout.activity_item_list)
    }

    private fun useCamera(){

        findViewById<Button>(R.id.opCamera).setOnClickListener {
            if (it.callOnClick()) {
                var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivity(intent)
            }
        }

    }

}