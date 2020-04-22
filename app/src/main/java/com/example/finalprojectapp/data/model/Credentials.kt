package com.example.finalprojectapp.data.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.firebase.firestore.Exclude

@Entity(tableName = "credentials_",indices = [Index(value = ["innerHashValue"],unique = true)])
data class Credentials (
    var hint:List<String>,
    var data:String,
    var innerHashValue:String?=null,
    var iv:String?=null,
    val salt:String?=null,
    @PrimaryKey(autoGenerate = true)
    @Exclude
    var credentialsId: Long=0
){
    constructor() : this(
        mutableListOf<String>(),
        ""
    )
}