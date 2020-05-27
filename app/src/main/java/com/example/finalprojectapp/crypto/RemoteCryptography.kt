package com.example.finalprojectapp.crypto

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.example.finalprojectapp.data.model.Credentials
import com.example.finalprojectapp.data.model.DataSet
import com.example.finalprojectapp.data.model.Service
import java.security.SecureRandom
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject

class RemoteCryptography @Inject constructor(context: Context?) : InnerCryptography() {

    private val password: String = "password"
    private val hashBuilder = HashBuilder()
    private val massageEncoder = Base64.getEncoder()
    private val massageDecoder = Base64.getDecoder()
    private var instance: SharedPreferences? = null
    private val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
    private val cipher: Cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
    private val keyFactory: SecretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")


    init {
        if (context != null) {
            instance = EncryptedSharedPreferences.create(
                "PreferencesFilename",
                MasterKeys.getOrCreate(keyGenParameterSpec),
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )

        }
    }

    private fun remoteDecryptSingle(credentials: Credentials?): Credentials? {
        var new = credentials ?: return null
        new = new.copy()
        val keySpec = PBEKeySpec(
            password.toCharArray(),
            massageDecoder.decode(new.salt),
            65536,
            256
        )
        val secretBytes: ByteArray = keyFactory.generateSecret(keySpec).encoded
        val key = SecretKeySpec(secretBytes, "AES")
        val ivSpec = IvParameterSpec(massageDecoder.decode(new.iv))
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec)
        val tempData =
            cipher.doFinal(massageDecoder.decode(new.data.toByteArray(Charsets.UTF_8)))
                .toString(Charsets.UTF_8)
        return new.copy(
            data = tempData,
            iv = null,
            salt = null
        )
    }


    private fun remoteEncryptCredential(credentials: Credentials?): Credentials? {
        var newCredentials: Credentials = credentials ?: return null
        if (credentials.innerHashValue.isNullOrEmpty())
            newCredentials = hashBuilder.makeHash(credentials) as Credentials
        if (!newCredentials.iv.isNullOrEmpty())
            return newCredentials
        val salt = ByteArray(16)
        SecureRandom().nextBytes(salt)
        val iv = ByteArray(16)
        SecureRandom().nextBytes(iv)
        val keySpec = PBEKeySpec(password.toCharArray(), salt, 65536, 256)
        val secretBytes: ByteArray = keyFactory.generateSecret(keySpec).encoded
        val key = SecretKeySpec(secretBytes, "AES")
        cipher.init(Cipher.ENCRYPT_MODE, key, IvParameterSpec(iv))
        return newCredentials.copy(
            data = massageEncoder.encodeToString(
                cipher.doFinal(
                    newCredentials.data.toByteArray(
                        Charsets.UTF_8
                    )
                )
            ),
            iv = massageEncoder.encodeToString(iv),
            salt = massageEncoder.encodeToString(salt)
        )

    }


    @Suppress("UNCHECKED_CAST")
    fun <T> remoteEncryption(target: T): T? {
        return when (target) {
            is Credentials -> remoteEncryptCredential(target) as T
            is DataSet -> this.encrypt(target) as T
            is Service -> this.encrypt(target) as T
            else -> null
        }
    }


    @Suppress("UNCHECKED_CAST")
    fun <T> remoteDecryption(target: T): T? {
        return when (target) {
            is Credentials -> remoteDecryptSingle(target) as T
            is DataSet -> this.decryption(target) as T
            is Service -> this.decryption(target) as T
            else -> null
        }
    }

}