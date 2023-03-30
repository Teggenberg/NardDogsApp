package com.example.narddogsinventory

data class ActiveListing(
    val age: Int? = 0,
    val brand : String? = null,
    val category : String? = null,
    val cost : Float? = null,
    val estRetail : Float? = null,
    val imageURL : String? = null,
    val itemDesc : String? = null,
    val itemID : Long? = null,
    val notes : String? = null,
    val user : Long? = null) {
}