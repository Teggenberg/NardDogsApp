package com.example.narddogsinventory

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.annotation.RequiresApi
import com.example.narddogsinventory.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDateTime
import java.util.*

private var currentUser : EntryUser? = null
val photoId = currentUser?.email + currentUser?.currentListing.toString()

class ModifyItem : AppCompatActivity() {

    private lateinit var bNav : NavigationBarView
    private val REQUEST_IMAGE_CAPTURE = 2
    private lateinit var captureButton: Button
    private  lateinit var binding: ActivityMainBinding
    private var currentFile: Uri? = null
    var imageReference = Firebase.storage.reference
    var imagevalURL = " "
    private var currentItem : ActiveListing? = null
    private var modifiedItem : ActiveListing? = null

    private lateinit var itemNum : TextView
    private lateinit var itemCost : EditText
    private lateinit var itemDesc : EditText
    private lateinit var itemBrand : EditText

    private lateinit var itemRetail : EditText
    private lateinit var itemCond : AutoCompleteTextView
    private lateinit var itemCat : AutoCompleteTextView
    private lateinit var itemNotes : EditText

    var photoTaken = false
    var imageID : String = " "

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_item)

        //      CONDITIONS
        // get reference to the string array that we just created
        val con = resources.getStringArray(R.array.conditions)
        // create an array adapter and pass the required parameter
        // in our case pass the context, drop down layout , and array.
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_text, con)
        // get reference to the autocomplete text view
        val autocompleteTV = findViewById<AutoCompleteTextView>(R.id.itDropDownCat)
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




        captureButton = findViewById(R.id.opCamera)
//        imageView = findViewById(R.id.imageView)

        captureButton.setOnClickListener {
            takePicture()
            photoTaken = true
        }






        //assign variable to textview to capture current item ID index
        itemNum = findViewById(R.id.etItemID)

        //capture user data passed from previous activity for db access and updating
        currentUser = intent.getParcelableExtra("currentUser", EntryUser::class.java)
        currentItem = intent.getParcelableExtra("currentItem", ActiveListing::class.java)

        imageID = currentUser?.email + currentItem?.itemID.toString()


        //store currentListing (item ID) into variable to assign to textview
        var itemid = currentItem?.itemID

        //set textview to display the current item ID to be assigned to new listing
        itemNum.setText("$itemid")

        //assign values from widgets into variables for data collection of new item
        itemBrand = findViewById(R.id.etBrand)
        itemDesc = findViewById<EditText>(R.id.etItemName)
        itemCat = findViewById(R.id.etDropDownBox)
        itemCond = findViewById(R.id.itDropDownCat)
        itemCost = findViewById<EditText>(R.id.etCost)
        itemRetail = findViewById(R.id.etRetail)
        itemNotes = findViewById(R.id.etItemNotes)

        itemBrand.setText(currentItem?.brand)
        itemDesc.setText(currentItem?.itemDesc)
        itemCond.setText(autocompleteTV.adapter.getItem(5 - currentItem?.condition!!).toString(), false)
        itemCat.setText(auto.adapter.getItem(categoryToInt(currentItem?.category)).toString(), false)
        itemCost.setText(currentItem?.cost.toString())
        itemRetail.setText(currentItem?.estRetail.toString())
        itemNotes.setText(currentItem?.notes)

        //assign bottom navigation bar to variable
        bNav = findViewById(R.id.bottomNav)

        //ensure the current activity is selected on navigation bar
        bNav.selectedItemId = R.id.AddItem

        //add new item into db when button is clicked
        //then increment user's current listing field in db, and local user's item ID
        //clear fields in widgets,and update textview with new item ID assignment
        findViewById<Button>(R.id.addModItem).setOnClickListener{


            addItemToDb()
            updateUserData(itemCost.text.toString().toFloat())

            currentItem = modifiedItem

            val rtnViewItem = Intent(this, ViewItem::class.java)
            rtnViewItem.putExtra("currentUser", currentUser)
            rtnViewItem.putExtra("currentItem", currentItem)
            startActivity(rtnViewItem)
            finish()




        }

        //bottom navigation bar listener
        //pass user object to next activity have local access to data
        bNav.setOnItemSelectedListener {

            when(it.itemId) {

                R.id.home -> {
                    val homeIntent = Intent(this, LoginActivity::class.java)
                    homeIntent.putExtra("currentUser", currentUser)
                    startActivity(homeIntent)
                    finish()
                    return@setOnItemSelectedListener true
                }
                R.id.AddItem -> {
                    val addIntent = Intent(this, ItemList::class.java)
                    addIntent.putExtra("currentUser", currentUser)
                    startActivity(addIntent)
                    return@setOnItemSelectedListener true
                }
                R.id.Inventory -> {
                    val inventoryIntent = Intent(this, ViewInventory::class.java)
                    //inventoryIntent.putExtra("userID", userID)
                    inventoryIntent.putExtra("currentUser", currentUser)
                    startActivity(inventoryIntent)
                    return@setOnItemSelectedListener true
                }
                R.id.Sales -> {
                    val salesIntent = Intent(this, Sales::class.java)
                    salesIntent.putExtra("currentUser", currentUser)
                    startActivity(salesIntent)
                    return@setOnItemSelectedListener true

                }
                else -> {
                    return@setOnItemSelectedListener false
                }
            }
        }


    }

    private fun categoryToInt(category: String?): Int {

        when(category){
            "Electronics" -> return 0
            "Apparel" -> return 1
            "Media" -> return 2
            "Furniture/Appliances" -> return 3
            "Collectibles" -> return 4
            else -> return 5

        }

    }

    private fun validEntryInput(): Boolean {


        //james, this is the function
        if(itemBrand.text == null) return false
        if(itemDesc.text == null) return false
        if(itemCond.text == null) return false
        if(itemCat.text == null) return false
        return true


    }

    private fun takePicture() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            saveImageToFile(imageBitmap)
        }
    }




    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageID = currentUser?.email + currentUser?.currentListing.toString()
        val imageFileName = "JPEG_${imageID}_"
        val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)



        val image = File.createTempFile(
            imageID,  /* prefix */
            ".jpg",         /* suffix */
            storageDir      /* directory */
        )

        uploadImage(imageFileName)
        // Log the file path
        Log.d("createImageFile", "Created image file at: ${image.absolutePath}")
        //Show user photo was saved
        //Toast.makeText(this, "Image saved to ${image.absolutePath}", Toast.LENGTH_SHORT).show()

        // Return the file
        return image
    }


    private fun saveImageToFile(bitmap: Bitmap) {
        val file = createImageFile()
        val outputStream: OutputStream? = FileOutputStream(file)

        Log.d("saveImageToFile", "Saving image to file: ${file.absolutePath}")

        outputStream?.let {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            it.flush()
            it.close()

            // Add the image to the device's gallery
            val values = ContentValues()
            //values.put(MediaStore.Images.Media.TITLE, file.name)
            values.put(MediaStore.Images.Media.TITLE, imageID)
            values.put(MediaStore.Images.Media.DESCRIPTION, "Image captured by camera")
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
            //values.put(MediaStore.Images.ImageColumns.BUCKET_ID, file.toString().toLowerCase().hashCode())
            values.put(MediaStore.Images.ImageColumns.BUCKET_ID, file.toString())
            values.put(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, file.name)

        }


/*  Firebase Code below   */

        // Get the file path
        val filePath = file.absolutePath

        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child("images/${imageID}")
        val stream = FileInputStream(File(filePath))
        val uploadTask = imageRef.putStream(stream)
        uploadTask.addOnSuccessListener {
            Log.d("saveImageToFile", "Image uploaded successfully to Firebase Storage")
            // Get the download URL and store it in imagevalURL
            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let { throw it }
                }
                imageRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    val imageUrl = downloadUri.toString()

                    imagevalURL = imageUrl
                    Log.d("saveImageToFile", "Image URL: $imageUrl")

                } else {
                    Log.e("saveImageToFile", "Error getting download URL", task.exception)
                }
            }
        }.addOnFailureListener {
            Log.e("saveImageToFile", "Error uploading image to Firebase Storage", it)
        }

        /*   Firebase Code above   */
    }


    private fun uploadImage(filename:String){
        try {
            currentFile?.let {
                imageReference.child("Pictures").putFile(it).addOnSuccessListener {
                    Toast.makeText(this,"Uploaded Successfully", Toast.LENGTH_SHORT)
                }.addOnFailureListener{
                    Toast.makeText(this,"Failed to upload", Toast.LENGTH_SHORT)
                }
            }
        }catch (e:java.lang.Exception){
            Toast.makeText(this,e.toString(),Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUserData(cost: Float) {

        //access to users database, reference document assigned to current user
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("Users").document(currentUser?.email.toString())

        val diff = currentItem?.cost!! - modifiedItem?.cost!!

        

        //access the document for the current user in database
        docRef.get().addOnSuccessListener { documentsnapshot ->

            //assign document to object for data access
            var updateCurrentListing = documentsnapshot.toObject<EntryUser>()

            //check to make sure document is not null
            if (updateCurrentListing != null) {



//                updateCurrentListing?.totInvested = updateCurrentListing?.totInvested?.
//                minus(currentItem?.cost!!)

                updateCurrentListing?.totInvested = updateCurrentListing?.totInvested?.
                minus(diff)


                //overwrite the fields in the database with updated values
                db.collection("Users").document(currentUser?.email.toString()).
                set(updateCurrentListing)

            }

        }

        //update currentListing for locally accessible user data to match database
        //currentUser?.totInvested = currentUser?.totInvested?.minus(currentItem?.cost!!)
        //currentUser?.totListings = currentUser?.totListings?.plus(1)
        currentUser?.totInvested = currentUser?.totInvested?.minus(diff)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun addItemToDb() {

        //capture all values from widgets to update data members for ActiveListing object
        val age = currentItem?.age
        val brand = itemBrand.text.toString()
        val category = itemCat.text.toString()
        val condition = conditionRating(itemCond.text.toString())
        val cost  = itemCost.text.toString().toFloatOrNull()
        val estRetail = itemRetail.text.toString().toFloatOrNull()
        val imageURL = "$imagevalURL" //this will include the url for the photo when complete
        val itemDesc = itemDesc.text.toString()
        val itemId = currentItem?.itemID
        val notes = itemNotes.text.toString()
        val user = currentUser?.userID

        //access to database
        val db = FirebaseFirestore.getInstance()

        //store all values into custom class object
        modifiedItem = ActiveListing(age, brand, category, condition, cost, estRetail, imageURL, itemDesc,
            itemId, notes, user)

        val docId = currentUser?.email + currentItem?.itemID.toString()

        //add document into itemListings collection using custom class object
        db.collection("itemListings").document(docId).set(modifiedItem!!)

        Toast.makeText(this,"Item successfully updated", Toast.LENGTH_LONG)
            .show()

    }

    private fun conditionRating(rating : String?): Int? {

        //variable for switch to assign value
        val condition : Int?
        //reads text in the 'condition' drop down
        //val rating = itemCond.text.toString()

        //switch to convert string to Int
        when(rating){
            "Excellent" -> condition = 5
            "Great" -> condition = 4
            "Fair" -> condition = 2
            "Roughed Up" -> condition = 1
            else -> condition = 3
        }

        //return Int for db assignment
        return condition

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateAge(): Int? {

        //use 01-01-23 as constant origin point
        val origin = LocalDateTime.parse("2023-01-01T20:00:00.0000")

        //current date captured when item is added
        val current = LocalDateTime.now()

        //return the number of days between origin and current date
        return Duration.between(origin,current).toDays().toInt()



    }
}