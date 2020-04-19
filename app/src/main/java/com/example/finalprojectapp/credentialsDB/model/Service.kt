package com.example.finalprojectapp.credentialsDB.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.finalprojectapp.data.model.Credentials
import com.example.finalprojectapp.localDB.CredentialsDAO
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp

@Entity(tableName = "service_",indices = [Index(value = ["name"],unique = true)])
data class Service (
    var name:String,
    @Ignore
    @ServerTimestamp
    val time: Timestamp?,
    @Ignore
    val userId: String?,
    @Ignore
    var dataSets: List<DataSet>?,
    @Exclude
    @PrimaryKey(autoGenerate = true) var serviceId: Long=0
){
    constructor() : this(
        "",
        null,
        null,
        null
    )

}