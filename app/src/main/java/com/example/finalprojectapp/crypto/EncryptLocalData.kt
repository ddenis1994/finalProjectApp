package com.example.finalprojectapp.crypto

import androidx.security.crypto.MasterKeys
import java.security.Key
import java.security.KeyStore
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.IvParameterSpec

class EncryptLocalData {

    private val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC

    fun encrypt(data:String):Pair<String,String> {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, getKey())

        return Pair(
            Base64.getEncoder().encodeToString(cipher.doFinal(data.toByteArray())),
            Base64.getEncoder().encodeToString(cipher.iv))
    }
    private fun getKey(): Key {
        val ks: KeyStore = KeyStore.getInstance("AndroidKeyStore").apply {
            load(null)
        }
        val masterKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)
        return ks.getKey(masterKeyAlias,null)
    }
    fun decrypt(data:String,iv:String):String{
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val t=Base64.getDecoder().decode(iv)
        val spec2= GCMParameterSpec(128,Base64.getDecoder().decode(iv))
        IvParameterSpec(Base64.getDecoder().decode(iv))
        cipher.init(Cipher.DECRYPT_MODE, getKey(),spec2)

        return cipher.doFinal(Base64.getDecoder().decode(data)).toString(Charsets.UTF_8)


    }



}