package com.example.finalprojectapp.data.model.adpters

import androidx.room.ColumnInfo

data class LayoutServiceView (
    @ColumnInfo(name = "name") val serviceName: String?,
    @ColumnInfo(name = "dataSetName") val dataSetName: String?,
    @ColumnInfo(name = "dataSetId") val dataSetId: Long?
)