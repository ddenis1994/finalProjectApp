package com.example.finalprojectapp.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class LocalServices (
    @Embedded val service: Service,
    @Relation(
        parentColumn = "serviceId",
        entityColumn = "serviceId"
    )
    var credentials: List<Credentials>
)
