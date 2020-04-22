package com.example.finalprojectapp.data.model

import androidx.room.ColumnInfo

class ViewCredentialData (
    @ColumnInfo(name = "data") val data: String?,
    @ColumnInfo(name = "iv") val iv: String?,
    @ColumnInfo(name = "hint") val hintList: List<String>?
)