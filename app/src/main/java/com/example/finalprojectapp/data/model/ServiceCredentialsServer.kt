package com.example.finalprojectapp.data.model

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser

data class ServiceCredentialsServer(
    val name: String?,
    val time : Timestamp,
    //TODO return the user id in production
    //val userId: FirebaseUser,
    var credentials: MutableList<Map<String,Any>>
)