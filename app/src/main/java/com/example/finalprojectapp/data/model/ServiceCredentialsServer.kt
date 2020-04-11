package com.example.finalprojectapp.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class ServiceCredentialsServer(
    val name: String,
    @ServerTimestamp
    val time: Timestamp?,
    val userId: String,
    val credentials: MutableList<Credentials>?
){
        constructor() : this(
            "",
            null,
            "",
            null
        )

}