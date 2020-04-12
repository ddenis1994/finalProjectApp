package com.example.finalprojectapp.data.model

import androidx.room.*
import com.example.finalprojectapp.localDB.LocalCredentialsDAO
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp

@Entity(tableName = "service",indices = [Index(value = ["name"],unique = true)])
data class Service (
    var name:String,
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
        null,
        null,
        null
    )
    constructor(temp: LocalCredentialsDAO.LocalServices):this(
        temp.service.name,
        null,
        null,
        temp.credentials,
        temp.service.serviceId
    )

}