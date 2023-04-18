package com.example.narddogsinventory

data class SalesCategory(
    val category : String? = null,
    var totItems : Int? = null,
    var totDollars : Float? = null,
    var margin : Float? = null,
    var averageSale : Int? = null,
    var totCost : Float? = null
)
