package com.example.finalstudy.crypto

import java.security.MessageDigest

class LoginPasswordHash(
        private val passwordRaw: String
        ,private val salt:ByteArray) {

    fun getSalt(): ByteArray {
        return salt
    }
    fun getRawPassword(): String {
        return passwordRaw
    }

    fun generateDigest(): ByteArray {
            val byteArrayPassword = passwordRaw.toByteArray()
            val md: MessageDigest = MessageDigest.getInstance("SHA-256")
            val result=md.digest(byteArrayPassword)
            return result
    }
}