package com.example.finalstudy.crypto

import android.util.Log
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.PBEParameterSpec


class GenarePasswordToIncrupt(val password:String) {

    val TAG="genarateString"
    fun genaratePassword(): Cipher? {
        val salt = ByteArray(8)


        val pbeParameterSpec = PBEParameterSpec(salt, 100)
        val keySpec=PBEKeySpec(this.password.toCharArray())
        val SecretKeyFactory  =SecretKeyFactory.getInstance("PBEWITHSHA256AND256BITAES-CBC-BC")
        val key=SecretKeyFactory.generateSecret(keySpec)
        val cipher = Cipher.getInstance("PBEWITHSHA256AND256BITAES-CBC-BC")
        val iv = ByteArray(16)
        SecureRandom().nextBytes(iv)
        cipher.init(Cipher.ENCRYPT_MODE, key,pbeParameterSpec)

        val ciphertext: ByteArray = cipher.doFinal("text to encrypt".toByteArray())
        Log.i(TAG,ciphertext.toString())
        cipher.init(Cipher.DECRYPT_MODE, key,pbeParameterSpec)
        val pciphertext= cipher.doFinal(ciphertext)
        Log.i(TAG,pciphertext.toString())


        return cipher





    }
}