package com.example.finalprojectapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "passwords")
data class LocalCredentials (
    @PrimaryKey(autoGenerate = true) val credentialsId: Long=0,
    val serviceId: Long,
    val hint:String,
    val value:String
)