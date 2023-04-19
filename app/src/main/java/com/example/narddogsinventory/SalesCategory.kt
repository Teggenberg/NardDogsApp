package com.example.narddogsinventory


data class SalesCategory(
    val category : String? = null,
    var totItems : Int = 0 ,
    var totDollars : Float = 0f,
    var margin : Float? = null,
    var averageSale : Float? = null,
    var totCost : Float = 0f
)

