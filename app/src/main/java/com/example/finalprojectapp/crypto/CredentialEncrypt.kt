package com.example.finalprojectapp.crypto

import com.example.finalprojectapp.data.model.Credentials
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


    fun encryptAll(plainText: MutableList<Credentials>?): MutableList<Credentials> {
        if (plainText.isNullOrEmpty())
            return mutableListOf()
        val encryptedData: MutableList<Credentials> = mutableListOf()
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
            cipher.update(it.data.toByteArray(Charsets.UTF_8))
            encryptedData.add(
                Credentials(
                    it.hint,
                    encoder.encodeToString(cipher.doFinal()),
                    encoder.encodeToString(iv),
                    encoder.encodeToString(salt)
                )
            )
        }
        return encryptedData
    }
    fun decryptAll(data: MutableList<Credentials>?): MutableList<Credentials> {
        val decryptedData: MutableList<Credentials> = mutableListOf()
        if (data.isNullOrEmpty())
            return decryptedData
        val decoder = Base64.getDecoder()
        data.forEach { decryptedMap ->
            if (decryptedMap.iv.isNullOrEmpty() or decryptedMap.salt.isNullOrEmpty()) {
                decryptedData.add(
                    Credentials(
                        decryptedMap.hint,
                        cipher.doFinal().toString(Charsets.UTF_8),
                        null,
                        null
                    )
                )
            } else {
                keySpec = PBEKeySpec(
                    password.toCharArray(),
                    decoder.decode(decryptedMap.salt.toString()),
                    65536,
                    256
                )
                secretBytes = keyFactory.generateSecret(keySpec).encoded
                key = SecretKeySpec(secretBytes, "AES")
                cipher.init(
                    Cipher.DECRYPT_MODE,
                    key,
                    IvParameterSpec(decoder.decode(decryptedMap.iv.toString()))
                )

                var temp=cipher.doFinal(
                    decoder.decode(
                    decryptedMap.data.toByteArray(Charsets.UTF_8)
                )
                ).toString(Charsets.UTF_8)
                decryptedData.add(
                    Credentials(
                        decryptedMap.hint,
                        temp,
                        null,
                        null
                    )

                )
            }
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