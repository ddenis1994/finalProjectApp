package com.example.finalstudy.crypto

import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

class FirstTimeGeneratedKeyAES(password:String){

    private val salt = "ssshhhhhhhhhhh!!!!"
    private val cipher: Cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
    private val keySpec: PBEKeySpec
    val keyFactory: SecretKeyFactory
    val keyS: SecretKey
    val key: SecretKeySpec

    init {

            val keyGenerator=KeyGenerator.getInstance("PBKDF2WithHmacSHA256","AndroidKeyStore")

            val bytePassword=password.toCharArray()
            keySpec= PBEKeySpec(bytePassword,salt.toByteArray(), 65536, 256)
            keyFactory=SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256","AndroidKeyStore")
            keyS=keyFactory.generateSecret(keySpec)

            key= SecretKeySpec(keyS.encoded,"AES")

        val ks:KeyStore= KeyStore.getInstance("AndroidkeyStore").apply {
            load(null)
        }



    }
}