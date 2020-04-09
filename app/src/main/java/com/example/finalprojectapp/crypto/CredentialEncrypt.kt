package com.example.finalprojectapp.crypto

import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import kotlin.collections.HashMap

class CredentialEncrypt(private val password:String) {

    private val cipher: Cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
    private var keySpec: PBEKeySpec? = null
    private val keyFactory: SecretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
    private var secretBytes: ByteArray = ByteArray(16)
    private var key: SecretKeySpec? = null


    fun encryptAll(plainText: MutableList<Map<String, Any>>?): MutableList<Map<String, Any>> {
        if (plainText.isNullOrEmpty())
            return mutableListOf()
        val encryptedData: MutableList<Map<String, Any>> = mutableListOf()
        val encoder = Base64.getEncoder()
        plainText.forEach {
            val salt = ByteArray(16)
            val iv = ByteArray(16)
            SecureRandom().nextBytes(salt)
            SecureRandom().nextBytes(iv)
            keySpec = PBEKeySpec(password.toCharArray(), salt, 65536, 256)
            secretBytes = keyFactory.generateSecret(keySpec).encoded
            key = SecretKeySpec(secretBytes, "AES")
            cipher.init(Cipher.ENCRYPT_MODE, key, IvParameterSpec(iv))
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
    fun decryptAll(data: MutableList<Map<String, Any>>?): MutableList<Map<String, Any>> {
        val decryptedData: MutableList<Map<String, Any>> = mutableListOf()
        if (data.isNullOrEmpty())
            return decryptedData
        val decoder = Base64.getDecoder()
        data.forEach { decryptedMap ->
            keySpec = PBEKeySpec(
                password.toCharArray(),
                decoder.decode(decryptedMap["salt"].toString()),
                65536,
                256
            )
            secretBytes = keyFactory.generateSecret(keySpec).encoded
            key = SecretKeySpec(secretBytes, "AES")
            cipher.init(
                Cipher.DECRYPT_MODE,
                key,
                IvParameterSpec(decoder.decode(decryptedMap["iv"].toString()))
            )
            cipher.update(
                decoder.decode(
                    decryptedMap["data"].toString().toByteArray(Charsets.UTF_8)
                )
            )
            decryptedData.add(
                hashMapOf(
                    "hint" to decryptedMap["hint"].toString(),
                    "data" to cipher.doFinal().toString(Charsets.UTF_8)
                )
            )
        }
        return decryptedData
    }
    fun decrypt(data: HashMap<String, Any>?): HashMap<String, Any> {
        val decoder = Base64.getDecoder()
        keySpec = PBEKeySpec(
            password.toCharArray(),
            decoder.decode(data?.get("salt").toString()),
            65536,
            256
        )
        secretBytes = keyFactory.generateSecret(keySpec).encoded
        key = SecretKeySpec(secretBytes, "AES")
        cipher.init(
            Cipher.DECRYPT_MODE,
            key,
            IvParameterSpec(decoder.decode(data!!["iv"].toString()))
        )
        cipher.update(
            decoder.decode(
                data["data"].toString().toByteArray(Charsets.UTF_8)
            ))
            return hashMapOf(
                "hint" to data["hint"].toString(),
                "data" to cipher.doFinal().toString(Charsets.UTF_8))
    }


}