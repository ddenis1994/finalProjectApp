package com.example.finalprojectapp.data.model

data class Credentials (
    val credentialsId: Long,
    val serviceId: Long,
    val hint:String,
    val value:String,
    val IV:String,
    val salt:String
)