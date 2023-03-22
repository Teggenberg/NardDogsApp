package com.example.narddogsinventory

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.DropBoxManager
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.android.material.navigation.NavigationBarView

class ItemList : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_list)


//      CONDITIONS
        // get reference to the string array that we just created
        val con = resources.getStringArray(R.array.conditions)
        // create an array adapter and pass the required parameter
        // in our case pass the context, drop down layout , and array.
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_text, con)
        // get reference to the autocomplete text view
        val autocompleteTV = findViewById<AutoCompleteTextView>(R.id.etDropDownCon)
        // set adapter to the autocomplete tv to the arrayAdapter
        autocompleteTV.setAdapter(arrayAdapter)

//      CATEGORIES
        // get reference to the string array that we just created
        val cat = resources.getStringArray(R.array.categories)
        // create an array adapter and pass the required parameter
        // in our case pass the context, drop down layout , and array.
        val adapter = ArrayAdapter(this, R.layout.dropdown_text, cat)
        // get reference to the autocomplete text view
        val auto = findViewById<AutoCompleteTextView>(R.id.etDropDownBox)
        // set adapter to the autocomplete tv to the arrayAdapter
        auto.setAdapter(adapter)


//      Open CAMERA

        findViewById<Button>(R.id.opCamera).setOnClickListener{

            var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivity(intent)
        }

    }


}