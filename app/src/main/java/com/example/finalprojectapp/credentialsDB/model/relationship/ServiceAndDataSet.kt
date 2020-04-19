package com.example.finalprojectapp.credentialsDB.model.relationship

import androidx.room.Embedded
import androidx.room.Relation
import com.example.finalprojectapp.credentialsDB.model.DataSet
import com.example.finalprojectapp.credentialsDB.model.Service

data class ServiceAndDataSet (
    @Embedded val service: Service,
    @Relation(
        parentColumn = "serviceId",
        entityColumn = "dataSetId"
    )
    val library: List<DataSet>
)