package com.example.finalprojectapp.data.model

import com.google.firebase.Timestamp

data class ServiceCredentialsServer(
    var name: String,
    val time: Timestamp?,
    val userId: String,
    var credentials: MutableList<Credentials>?
){
        constructor() : this(
            "",
            null,
            "",
            null
        )

}