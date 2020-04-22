package com.example.finalprojectapp.data

import androidx.room.ColumnInfo

data class ViewServiceData (
    @ColumnInfo(name = "name") val serviceName: String?,
    @ColumnInfo(name = "dataSetName") val dataSetName: String?,
    @ColumnInfo(name = "dataSetId") val dataSetId: Long?
)