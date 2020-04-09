package com.example.finalprojectapp.data.model

data class Credentials (
    val hint:List<String>,
    val data:String,
    val iv:String?,
    val salt:String?
){
    constructor() : this(
        mutableListOf<String>(),
        "",
        "",
        ""
    )

}