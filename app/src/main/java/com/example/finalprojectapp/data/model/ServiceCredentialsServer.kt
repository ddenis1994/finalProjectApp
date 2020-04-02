package com.example.finalprojectapp.data.model

import com.google.firebase.Timestamp

data class ServiceCredentialsServer(
    val name: String,
    val time: Timestamp,
    val userId: String,
    var credentials: MutableList<Map<String, Any>>?
)