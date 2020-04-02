package com.example.finalprojectapp.crypto

import com.example.finalprojectapp.data.model.ServiceCredentialsServer
import java.security.SecureRandom
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

class CredentialEncrypt(private val password:String) {

    private val cipher: Cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
    private var keySpec: PBEKeySpec? =null
    private val keyFactory:SecretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
    private var secretBytes:ByteArray = ByteArray(16)
    private var key: SecretKeySpec? =null


    fun encryptAll (plainText: MutableList<Map<String, Any>>?): MutableList<Map<String, Any>> {
        if (plainText.isNullOrEmpty())
            return mutableListOf()
        val encryptedData: MutableList<Map<String, Any>> = mutableListOf()
        val encoder=Base64.getEncoder()
        plainText.forEach {
            val salt = ByteArray(16)
            val iv =ByteArray(16)
            SecureRandom().nextBytes(salt)
            SecureRandom().nextBytes(iv)
            keySpec= PBEKeySpec(password.toCharArray(),salt, 65536, 256)
            secretBytes=keyFactory.generateSecret(keySpec).encoded
            key= SecretKeySpec(secretBytes,"AES")
            cipher.init(Cipher.ENCRYPT_MODE, key,IvParameterSpec(iv))
            cipher.update(it["data"].toString().toByteArray(Charsets.UTF_8))
            encryptedData.add(
                hashMapOf(
                    "hint" to it["hint"].toString(),
                    "data" to encoder.encodeToString(cipher.doFinal()),
                    "iv" to encoder.encodeToString(iv),
                    "salt" to encoder.encodeToString(salt)
                )
            )
        }
        return encryptedData
    }
    fun decryptAll(data:MutableList<Map<String, Any>>?): MutableList<Map<String, Any>> {
        val decryptedData: MutableList<Map<String, Any>> = mutableListOf()
        if (data.isNullOrEmpty())
            return decryptedData
        val decoder=Base64.getDecoder()
        data.forEach {decryptedMap->
            keySpec= PBEKeySpec(password.toCharArray(),decoder.decode(decryptedMap["salt"].toString()), 65536, 256)
            secretBytes=keyFactory.generateSecret(keySpec).encoded
            key= SecretKeySpec(secretBytes,"AES")
            cipher.init(Cipher.DECRYPT_MODE, key,IvParameterSpec(decoder.decode(decryptedMap["iv"].toString())))
            cipher.update(decoder.decode(decryptedMap["data"].toString().toByteArray(Charsets.UTF_8)))
            decryptedData.add(hashMapOf(
                "hint" to decryptedMap["hint"].toString(),
                "data" to cipher.doFinal().toString(Charsets.UTF_8)
                )
            )
        }
        return decryptedData
    }
/*
    fun decrypt(encryptCredentials: ServiceCredentialsServer): ServiceCredentialsServer {

        encryptCredentials.credentials!!.forEach {
            keySpec= PBEKeySpec(password.toCharArray(),Base64.getDecoder().decode(it["salt"].toString()), 65536, 256)
            secretBytes=keyFactory.generateSecret(keySpec).encoded
            key= SecretKeySpec(secretBytes,"AES")
            cipher.init(Cipher.DECRYPT_MODE, key,IvParameterSpec(Base64.getDecoder().decode(it["iv"].toString())))
            cipher.update(Base64.getDecoder().decode(it["data"].toString().toByteArray(Charsets.UTF_8)))
            val value: String =cipher.doFinal().toString(Charsets.UTF_8)
            decryptCredentials.add(hashMapOf(
                "hint" to it["hint"].toString(),
                "data" to value
                )
            )
        }
        encryptCredentials.credentials=decryptCredentials
        return encryptCredentials

    }

 */


}








/*
       keySpec= PBEKeySpec(password.toCharArray(),Base64.getDecoder().decode(encryptCredentials.salt), 65536, 256)
       secretBytes=keyFactory.generateSecret(keySpec).encoded
       key= SecretKeySpec(secretBytes,"AES")

       cipher.init(Cipher.DECRYPT_MODE, key,IvParameterSpec(Base64.getDecoder().decode(encryptCredentials.IV)))
       cipher.update(Base64.getDecoder().decode(encryptCredentials.value.toByteArray(Charsets.UTF_8)))
       val value: String =cipher.doFinal().toString(Charsets.UTF_8)
       return Credentials(
               encryptCredentials.serviceId,
               encryptCredentials.hint,
               value,
               "",
               ""
       )

        */