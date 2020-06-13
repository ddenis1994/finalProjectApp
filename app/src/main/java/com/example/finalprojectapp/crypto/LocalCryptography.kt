package com.example.finalprojectapp.crypto

import android.view.View
import androidx.security.crypto.MasterKeys
import com.example.finalprojectapp.data.model.Credentials
import com.example.finalprojectapp.data.model.DataSet
import com.example.finalprojectapp.data.model.Service
import java.security.Key
import java.security.KeyStore
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class LocalCryptography @Inject constructor(//private var instance: SharedPreferences? = null
    private val hashBuilder: HashBuilder
) : InnerCryptography() {

    private val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
    private val massageEncoder = Base64.getEncoder()
    private val massageDecoder = Base64.getDecoder()
    private val cipher = Cipher.getInstance("AES/GCM/NoPadding")

//    init {
//        if (context != null) {
//            instance = EncryptedSharedPreferences.create(
//                "PreferencesFilename",
//                MasterKeys.getOrCreate(keyGenParameterSpec),
//                context,
//                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
//                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
//            )
//
//        }
//    }


    private fun getKey(): Key {
        val ks: KeyStore = KeyStore.getInstance("AndroidKeyStore").apply {
            load(null)
        }
        val masterKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)
        return ks.getKey(masterKeyAlias, null)
    }


    private fun localEncryptCredentials(credentials: Credentials?): Credentials? {
        var newCredentials: Credentials = credentials ?: return null
        if (credentials.innerHashValue.isNullOrEmpty())
            newCredentials = hashBuilder.makeHash(credentials) as Credentials
        if (!newCredentials.iv.isNullOrEmpty())
            return newCredentials

        //TODO fix encrypt only if sensative data
//        val h=credentials.hint.filter { it == View.AUTOFILL_HINT_CREDIT_CARD_NUMBER || it == View.AUTOFILL_HINT_PASSWORD  }
//        if (h.isEmpty())
//            return newCredentials
        cipher.init(Cipher.ENCRYPT_MODE, getKey())
        val encryptedData = cipher.doFinal(newCredentials.data.toByteArray())
        return newCredentials.copy(
            data = massageEncoder.encodeToString(encryptedData),
            iv = massageEncoder.encodeToString(cipher.iv)
        )
    }

    override fun <T> encrypt(target: T): T? {
        return when (target) {
            is Credentials -> localEncryptCredentials(target) as T
            is DataSet -> encryptDataSet(target) as T
            is Service -> encryptService(target) as T
            else -> null
        }
    }




    private fun localDecryptCredential(credentials: Credentials?): Credentials? {
        var newCredential = credentials ?: return null
        newCredential = newCredential.copy()
        val iv = credentials.iv ?: return credentials
        val spec = GCMParameterSpec(128, Base64.getDecoder().decode(iv))
        cipher.init(Cipher.DECRYPT_MODE, getKey(), spec)
        val decryptedData = cipher.doFinal(
            massageDecoder.decode(
                credentials.data
            )
        )
        return newCredential.copy(
            iv = null, data = decryptedData.toString(Charsets.UTF_8)
        )
    }

    override fun <T> decryption(target: T): T? {
        return when (target) {
            is Credentials -> localDecryptCredential(target) as T
            is DataSet -> decryptDataSet(target) as T
            is Service -> decryptService(target) as T
            else -> null
        }
    }




}