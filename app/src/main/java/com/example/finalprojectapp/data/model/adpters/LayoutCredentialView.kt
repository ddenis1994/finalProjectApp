package com.example.finalprojectapp.data.model.adpters

import androidx.room.ColumnInfo

data class LayoutCredentialView (
    @ColumnInfo(name = "data") val data: String?,
    @ColumnInfo(name = "iv") val iv: String?,
    @ColumnInfo(name = "hint") val hintList: List<String>?,
    @ColumnInfo(name = "credentialsId") val id: Long=-1L
)