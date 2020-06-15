package com.example.finalprojectapp.data.model.relationship

import androidx.room.Entity


@Entity(primaryKeys = ["credentialsId","dataSetId"])
data class DataSetCredentialCrossRef (
    val credentialsId:Long,
    val dataSetId:Long
)