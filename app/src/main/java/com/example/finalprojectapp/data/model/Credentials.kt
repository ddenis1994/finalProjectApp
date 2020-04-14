package com.example.finalprojectapp.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.firebase.firestore.Exclude

@Entity(tableName = "passwords",indices = [Index(value = ["serviceId","hint","data"],unique = true)])
data class Credentials (
    val hint:List<String>,
    val data:String,
    val iv:String?=null,
    val salt:String?=null,
    @PrimaryKey(autoGenerate = true)
    @Exclude
    val credentialsId: Long?=0,
    @Exclude
    val serviceId:Long?=0
){
    constructor() : this(
        mutableListOf<String>(),
        "",
        null,
        null
    )

}