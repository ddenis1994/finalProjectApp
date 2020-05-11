package com.example.finalprojectapp.data.model

import com.example.finalprojectapp.adapters.DashBoardRecyclerRepeatedPasswordAdapter
import com.example.finalprojectapp.ui.dashboard.DashboardViewModel
import com.google.firebase.auth.FirebaseAuth

data class DashBoardData(
    val serviceCount: Int = 0,
    val securityRisks: Int =0,
    val connectionToRemote:Boolean= FirebaseAuth.getInstance().currentUser != null,
    val passwordStrange:Int =0,
    val repeatedPassport:Int=0,
    val passwordRepeated: Map<Long, List<DashboardViewModel.HashAndId>>? =null,
    var viewAdapter: DashBoardRecyclerRepeatedPasswordAdapter?=null
)