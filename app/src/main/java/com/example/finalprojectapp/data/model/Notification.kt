package com.example.finalprojectapp.data.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp

@Entity
data class Notification(
    var type:Int,
    var mainMassage:String="",
    var secondMassage:String="",
    var time:String?=null,
    @Ignore
    var timestamp: ServerTimestamp?=null,
    @PrimaryKey(autoGenerate = true)
    @Exclude
    var key:Long=0
){
    constructor() : this(-1)
}