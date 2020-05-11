package com.example.finalprojectapp.data.model

import com.google.firebase.auth.FirebaseAuth

data class DashBoardData (
    var serviceCount: Int = 0,
    val securityRisks: Int =0,
    val connectionToRemote:Boolean= FirebaseAuth.getInstance().currentUser != null,
    val passwordStrange:Int =0
)