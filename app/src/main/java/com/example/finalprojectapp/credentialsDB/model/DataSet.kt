package com.example.finalprojectapp.credentialsDB.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.finalprojectapp.credentialsDB.model.relationship.DataSetAndCredentials
import com.google.firebase.firestore.Exclude

@Entity(tableName = "dataSet_",indices = [Index(value = ["hashData"],unique = true)])
data class DataSet (
    @Ignore
    var credentials: List<Credentials>?=null,
    var hashData:String?=null,
    @PrimaryKey(autoGenerate = true)
    @Exclude
    var dataSetId: Long=0,
    @Exclude
    var serviceId:Long?=0
){
    constructor() : this(
        null
    )
    constructor(temp: DataSetAndCredentials):this(
        temp.credentials,
        temp.dataSet.hashData,
        temp.dataSet.dataSetId,
        temp.dataSet.serviceId

    )

}