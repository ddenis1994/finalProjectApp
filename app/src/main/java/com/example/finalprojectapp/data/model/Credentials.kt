package com.example.finalprojectapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.Exclude

@Entity(tableName = "passwords")
data class Credentials (
    @PrimaryKey(autoGenerate = true)
    @Exclude
    val credentialsId: Long=0,
    @Exclude
    val serviceId:Long,
    val hint:List<String>,
    val data:String,
    val iv:String?,
    val salt:String?
){
    constructor() : this(
        0,
        0,
        mutableListOf<String>(),
        "",
        "",
        ""
    )

}