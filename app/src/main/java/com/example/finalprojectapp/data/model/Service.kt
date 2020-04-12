package com.example.finalprojectapp.data.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp

@Entity(tableName = "service")
data class Service (
    var name:String,
    @Ignore
    @ServerTimestamp
    val time: Timestamp?,
    @Ignore
    val userId: String,
    @Ignore
    val credentials: MutableList<Credentials>?,
    @Exclude
    @PrimaryKey(autoGenerate = true) var serviceId: Long=0
){
    constructor() : this(
        "",
        null,
        "",
        null
    )

}