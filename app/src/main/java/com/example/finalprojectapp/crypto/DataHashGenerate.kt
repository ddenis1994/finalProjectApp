package com.example.finalprojectapp.crypto

import com.example.finalprojectapp.data.model.Service
import java.security.MessageDigest
import java.util.*

class DataHashGenerate {

    fun generateSHA256(data:Service): String {
        var rawData=String()
        data.credentials?.forEach {
            rawData+=it.data
            rawData+=it.hint
        }
        val message: ByteArray =rawData.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        return Base64.getEncoder().encodeToString(md.digest(message))

    }

}