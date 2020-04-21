package com.example.finalprojectapp.data.model.relationship

import androidx.room.Embedded
import androidx.room.Relation
import com.example.finalprojectapp.data.model.DataSet
import com.example.finalprojectapp.data.model.Service


data class ServiceToDataSet (
    @Embedded val service: Service,
    @Relation(
        parentColumn = "serviceId",
        entityColumn = "serviceId"
    )
    val dataSets: List<DataSet>
)