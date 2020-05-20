package com.example.finalprojectapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.Exclude

@Entity
data class Notification(
    var type:Int,
    var mainMassage:String,
    var secondMassage:String,
    val time:String,
    @PrimaryKey(autoGenerate = true)
    @Exclude
    var key:Long=0
)