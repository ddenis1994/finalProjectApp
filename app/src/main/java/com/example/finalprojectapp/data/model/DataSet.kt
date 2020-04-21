package com.example.finalprojectapp.data.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.firebase.firestore.Exclude

@Entity(tableName = "dataSet_",indices = [Index(value = ["hashData"])])
data class DataSet (
    @Ignore
    var credentials: List<Credentials>?=null,
    var hashData:String?=null,
    var dataSetName:String="",
    @PrimaryKey(autoGenerate = true)
    @Exclude
    var dataSetId: Long=0,
    @Exclude
    var serviceId:Long?=0
){
    constructor() : this(
        null
    )

}