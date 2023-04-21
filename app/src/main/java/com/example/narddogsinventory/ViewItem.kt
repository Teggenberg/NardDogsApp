package com.example.narddogsinventory

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

import android.widget.*

import androidx.annotation.RequiresApi
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage

import com.google.firebase.storage.StorageReference

import java.io.File
import java.io.IOException
import java.time.Duration
import java.time.LocalDateTime

class ViewItem : AppCompatActivity() {

    private lateinit var itemBrand: TextView
    private lateinit var itemDesc : TextView
    private lateinit var itemCategory : TextView
    private lateinit var itemCondition : TextView
    private lateinit var itemCost : TextView
    private lateinit var itemAge : TextView
    private lateinit var itemNotes : TextView
    private lateinit var itemId : TextView

    private  var currentItem : ActiveListing? = null
    private var currentUser : EntryUser? = null
    private lateinit var back : Button
    private lateinit var sold : Button
    private lateinit var price : EditText


    private lateinit var itemImage : ShapeableImageView

    private lateinit var imageRef : StorageReference


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_item)

        currentUser = intent.getParcelableExtra("currentUser", EntryUser::class.java)
        currentItem = intent.getParcelableExtra("currentItem", ActiveListing::class.java)

        back = findViewById(R.id.buttonReturn)
        sold = findViewById(R.id.buttonSold)
        price = findViewById(R.id.etSoldAmount)
        itemImage = findViewById(R.id.ivItem)

        itemImage = findViewById(R.id.ivItem)

        




        itemBrand = findViewById(R.id.tvItemBrand)
        itemDesc = findViewById(R.id.tvItemDesc)
        itemCategory = findViewById(R.id.tvItemCat)
        itemCondition = findViewById(R.id.tvItemCondition)
        itemCost = findViewById(R.id.tvItemCost)
        itemAge = findViewById(R.id.tvAge)
        itemNotes = findViewById(R.id.tvItemNotes)
        itemId = findViewById(R.id.tvItemID)

        itemDesc.text = currentItem?.itemDesc.toString()
        itemBrand.text = currentItem?.brand.toString()
        itemCategory.text = currentItem?.category.toString()
        itemCondition.text = decipherCondition(currentItem?.condition)
        itemCost.text = currentItem?.cost.toString()
        itemAge.text = calculateAge(currentItem?.age)
        itemNotes.text = currentItem?.notes.toString()
        itemId.text = currentItem?.itemID.toString()

        displayImage()


        back.setOnClickListener{

            val intent = Intent(this, ViewInventory::class.java)
            intent.putExtra("currentUser", currentUser)
            startActivity(intent)
        }
        sold.setOnClickListener{

            convertListing()
        }
    }

    private fun displayImage() {

        val imageTitle = currentUser?.email + currentItem?.itemID.toString()

        imageRef = FirebaseStorage.getInstance().getReference("images/$imageTitle")



        try {
            val localFile : File = File.createTempFile("tempfile", ".jpg")
            imageRef.getFile(localFile).addOnSuccessListener {



                    val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)

                    itemImage.setImageBitmap(bitmap)


            }.addOnFailureListener {

                Toast.makeText(this, "error getting image", Toast.LENGTH_SHORT).show()
                Toast.makeText(this, "$imageTitle", Toast.LENGTH_SHORT).show()
            }

        }catch (e: IOException){
            val error = e.toString()
            Toast.makeText(this, "$error", Toast.LENGTH_SHORT).show()

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun convertListing() {
        val db = FirebaseFirestore.getInstance()

        val soldDoc = db.collection("soldListings")

        val removeItem = db.collection("itemListings")

        val date = LocalDateTime.now().toString()

        val amount = price.text.toString().toFloatOrNull()

        val soldItem = SoldListing(currentItem, date, amount, currentUser?.userID)

        val docID = currentUser?.email + currentItem?.itemID

        soldDoc.document(docID).set(soldItem)

        removeItem.document(docID).delete()

        updateUser()

        val intent = Intent(this, ViewInventory::class.java)
        intent.putExtra("currentUser", currentUser)
        startActivity(intent)
    }

    private fun updateUser() {

        //access to users database, reference document assigned to current user
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("Users").document(currentUser?.email.toString())

        val amount = price.text.toString().toFloatOrNull()

        //access the document for the current user in database
        docRef.get().addOnSuccessListener { documentsnapshot ->

            //assign document to object for data access
            var updateCurrentListing = documentsnapshot.toObject<EntryUser>()

            //check to make sure document is not null
            if (updateCurrentListing != null) {




                updateCurrentListing?.totListings = updateCurrentListing?.totListings?.
                plus(-1)

                updateCurrentListing?.totInvested = updateCurrentListing?.totInvested?.
                minus(currentItem?.cost!!)

                updateCurrentListing?.totSales = updateCurrentListing?.totSales?.
                plus(amount!!)


                //overwrite the fields in the database with updated values
                db.collection("Users").document(currentUser?.email.toString()).
                set(updateCurrentListing)

            }

        }

        //update currentListing for locally accessible user data to match database

        currentUser?.totListings = currentUser?.totListings?.plus(-1)
        currentUser?.totInvested = currentUser?.totInvested?.minus(currentItem?.cost!!)
        currentUser?.totSales = currentUser?.totSales?.plus(amount!!)
    }



    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateAge(age: Int?): CharSequence? {

        //use 01-01-23 as constant origin point
        val origin = LocalDateTime.parse("2023-01-01T20:00:00.0000")

        //current date captured when item is added
        val current = LocalDateTime.now()

        //return the number of days between origin and current date
        val compare = Duration.between(origin,current).toDays().toInt()

        if(compare - age!! != 1) return (compare - age!!).toString() + " days"
        else return (compare - age!!).toString() + " day"

    }


    private fun decipherCondition(condition: Int?): CharSequence? {

        val rating : CharSequence?
        when(condition){

            1 -> rating = "Roughed Up"
            2 -> rating = "Fair"
            3 -> rating = "Good"
            4 -> rating = "Great"
            5 -> rating = "Excellent"
            else -> rating = " "
        }

        return rating

    }


}