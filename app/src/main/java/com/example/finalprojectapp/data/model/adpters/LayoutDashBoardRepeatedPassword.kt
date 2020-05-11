package com.example.finalprojectapp.data.model.adpters

import androidx.room.ColumnInfo

data class LayoutDashBoardRepeatedPassword (
    @ColumnInfo( name = "dataSetName") var dataSetName:String,
    @ColumnInfo( name = "serviceName") var serviceName:String
)