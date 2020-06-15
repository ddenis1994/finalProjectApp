package com.example.finalprojectapp.data.model.relationship

import androidx.room.Embedded
import androidx.room.Relation
import com.example.finalprojectapp.data.model.Credentials
import com.example.finalprojectapp.data.model.DataSet


data class DataSetWithCredentials(
    @Embedded val dataSet: DataSet= DataSet(),
    @Relation(
        parentColumn = "dataSetId",
        entityColumn = "credentialDataSetId"
    )
    val credentials: List<Credentials> = listOf()
)

