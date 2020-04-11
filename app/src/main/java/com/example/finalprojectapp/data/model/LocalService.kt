package com.example.finalprojectapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "services")

data class LocalService (
    @PrimaryKey(autoGenerate = true) val serviceId: Long=0,
    var serviceName:String
)