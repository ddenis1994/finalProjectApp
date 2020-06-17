package com.example.finalprojectapp.data.model

import com.google.firebase.auth.FirebaseAuth

data class DashBoardData(
    val connectionToRemote:Boolean= FirebaseAuth.getInstance().currentUser != null,
    val passwordStrange:Int =0,
    val repeatedPassport:Int=0
)