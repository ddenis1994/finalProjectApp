package com.example.finalprojectapp.credentialsDB.model.relationship

import androidx.room.Embedded
import androidx.room.Relation
import com.example.finalprojectapp.credentialsDB.model.Credentials
import com.example.finalprojectapp.credentialsDB.model.DataSet

data class DataSetAndCredentials (
    @Embedded val dataSet: DataSet,
    @Relation(
        parentColumn = "dataSetId",
        entityColumn = "dataSetId"
    )
    val credentials: List<Credentials>
)