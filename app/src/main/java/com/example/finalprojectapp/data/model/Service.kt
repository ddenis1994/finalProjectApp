package com.example.finalprojectapp.data.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp

@Entity(tableName = "service",indices = [Index(value = ["name","hashData"],unique = true)])
data class Service (
    var name:String,
    var hashData:String,
    @Ignore
    @ServerTimestamp
    val time: Timestamp?,
    @Ignore
    val userId: String?,
    @Ignore
    var credentials: List<Credentials>?,
    @Exclude
    @PrimaryKey(autoGenerate = true) var serviceId: Long=0
){
    constructor() : this(
        "",
        "",
        null,
        null,
        null
    )
}