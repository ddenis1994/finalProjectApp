package com.example.finalprojectapp.data.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp

@Entity(tableName = "service_",indices = [Index(value = ["name"],unique = true)])
data class Service (
    var name:String,
    var hash:String,
    @Ignore
    @ServerTimestamp
    val time: Timestamp?,
    @Ignore
    val userId: String?,
    @Ignore
    @Exclude
    var dataSets: List<DataSet>?,
    @Exclude
    @PrimaryKey(autoGenerate = true) var serviceId: Long=0
){
    constructor(name: String,dataSets: List<DataSet>?) : this(
        name,
        "",
        null,
        null,
        dataSets
    )

    constructor() : this(
        "",
        "",
        null,
        null,
        null
    )

}