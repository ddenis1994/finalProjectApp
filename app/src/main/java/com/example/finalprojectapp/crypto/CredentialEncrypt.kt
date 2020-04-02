package com.example.finalstudy.crypto

import com.example.finalprojectapp.data.model.Credentials
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
    private val salt:ByteArray = ByteArray(16)
    private val iv:ByteArray=ByteArray(16)

    fun encrypt (plainText: Credentials): Credentials {
        SecureRandom().nextBytes(salt)
        SecureRandom().nextBytes(iv)
        keySpec= PBEKeySpec(password.toCharArray(),salt, 65536, 256)
        secretBytes=keyFactory.generateSecret(keySpec).encoded
        key= SecretKeySpec(secretBytes,"AES")

        cipher.init(Cipher.ENCRYPT_MODE, key,IvParameterSpec(iv))
        cipher.update(plainText.value.toByteArray(Charsets.UTF_8))
        val cipherValue=cipher.doFinal()
        val encoder=Base64.getEncoder()
        return Credentials(
                plainText.credentialsId,
                plainText.serviceId,
                plainText.hint,
                encoder.encodeToString(cipherValue),
                encoder.encodeToString(iv),
                encoder.encodeToString(salt)
        )
    }
    fun decrypt(encryptCredentials: Credentials): Credentials {
        keySpec= PBEKeySpec(password.toCharArray(),Base64.getDecoder().decode(encryptCredentials.salt), 65536, 256)
        secretBytes=keyFactory.generateSecret(keySpec).encoded
        key= SecretKeySpec(secretBytes,"AES")

        cipher.init(Cipher.DECRYPT_MODE, key,IvParameterSpec(Base64.getDecoder().decode(encryptCredentials.IV)))
        cipher.update(Base64.getDecoder().decode(encryptCredentials.value.toByteArray(Charsets.UTF_8)))
        val value: String =cipher.doFinal().toString(Charsets.UTF_8)
        return Credentials(
                encryptCredentials.credentialsId,
                encryptCredentials.serviceId,
                encryptCredentials.hint,
                value,
                "",
                ""
        )
    }
}