package com.example.finalprojectapp.credentialsDB.model.relationship

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "dataSetCredentialsManyToMany",indices = [Index(value = ["credentialsId","dataSetId"],unique = true)])
data class DataSetCredentialsManyToMany (
    var dataSetId: Long=0,
    var credentialsId: Long=0,
    @PrimaryKey(autoGenerate = true)
    var DataSetCredentialsManyToManyID: Long=0
)