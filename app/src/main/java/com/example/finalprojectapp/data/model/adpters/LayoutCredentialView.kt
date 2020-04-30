package com.example.finalprojectapp.data.model.adpters

import androidx.room.ColumnInfo

class LayoutCredentialView (
    @ColumnInfo(name = "data") val data: String?,
    @ColumnInfo(name = "iv") val iv: String?,
    @ColumnInfo(name = "hint") val hintList: List<String>?
)