package com.example.finalprojectapp.data.model.adpters

import androidx.room.ColumnInfo

class LayoutDataSetView (
    @ColumnInfo(name = "dataSetName") val dataSetName: String,
    @ColumnInfo(name = "dataSetId") val dataSetId: Long
)