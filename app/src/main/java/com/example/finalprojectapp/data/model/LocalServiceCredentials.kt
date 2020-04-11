package com.example.finalprojectapp.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class LocalServiceCredentials (
    @Embedded val service: LocalService,
    @Relation(
        parentColumn = "serviceId",
        entityColumn = "serviceId"
    )
    var credentials: List<LocalCredentials>
)
