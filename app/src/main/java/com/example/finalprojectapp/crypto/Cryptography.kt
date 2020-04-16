package com.example.finalprojectapp.crypto

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.example.finalprojectapp.data.model.Credentials
import com.example.finalprojectapp.data.model.Service
import java.security.Key
import java.security.KeyStore
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

class Cryptography(context:Context?) {
    private var service:Service?=null
    private val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
    private val password:String="password"
    private var  instance:SharedPreferences?=null
    init {
        if (context!=null) {
            instance = EncryptedSharedPreferences.create(
                "PreferencesFilename",
                MasterKeys.getOrCreate(keyGenParameterSpec),
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )

        }
    }


    fun remoteEncryption(): Service? {
        if(sanityCheckRemote()) {
            if (service!!.credentials.isNullOrEmpty())
                return null
            val encryptedData= mutableListOf<Credentials>()
            val encoder = Base64.getEncoder()
            val cipher: Cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
            val keyFactory: SecretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            service!!.credentials!!.forEach {
                val salt = ByteArray(16)
                SecureRandom().nextBytes(salt)
                val iv = ByteArray(16)
                SecureRandom().nextBytes(iv)
                val keySpec =   PBEKeySpec(password.toCharArray(), salt, 65536, 256)
                val secretBytes: ByteArray = keyFactory.generateSecret(keySpec).encoded
                val key = SecretKeySpec(secretBytes, "AES")
                cipher.init(Cipher.ENCRYPT_MODE, key, IvParameterSpec(iv))
                encryptedData.add(
                    it.copy(
                        data = encoder.encodeToString(cipher.doFinal(it.data.toByteArray(Charsets.UTF_8))),
                        iv =encoder.encodeToString(iv),
                        salt =  encoder.encodeToString(salt))
                )
            }
            if (encryptedData.isEmpty())
                return null
            val temp=service!!.copy(credentials = encryptedData,hashData = generateSHA256())
            service=null
            return temp
        }
        return null
    }

    private fun sanityCheckRemote(): Boolean {
        return sanityCheck() and (instance!=null)
    }

    fun remoteDecrypt(): Service?{
        if(sanityCheckRemote()){
            if (service!!.credentials.isNullOrEmpty())
                return null
            val decryptedData: MutableList<Credentials> = mutableListOf()
            service!!.credentials?.forEach {cre->
                remoteDecryptSingle(cre)?.let { decryptedCre ->
                    decryptedData.add(decryptedCre)
                }
            }
            if (decryptedData.isEmpty())
                return null
            return service!!.copy(credentials = decryptedData)
        }
        return null
    }

    fun remoteDecryptSingle(data: Credentials?): Credentials? {
        if(data!=null && sanityCheckRemote()) {
            val keyFactory: SecretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            val cipher: Cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
            val decoder = Base64.getDecoder()
            val keySpec = PBEKeySpec(
                password.toCharArray(),
                decoder.decode(data.salt.toString()),
                65536,
                256
            )
            val secretBytes: ByteArray = keyFactory.generateSecret(keySpec).encoded
            val key = SecretKeySpec(secretBytes, "AES")
            val ivSpec=IvParameterSpec(Base64.getDecoder().decode(data.iv))
            cipher.init(
                Cipher.DECRYPT_MODE,
                key,
                ivSpec
            )
            val tempData=cipher.doFinal(decoder.decode(data.data.toByteArray(Charsets.UTF_8))).toString(Charsets.UTF_8)
            return data.copy(
                data = tempData,
                iv = null,
                salt = null
            )
        }
        return null
    }

    fun localEncrypt():Service?{
        if(sanityCheck()) {
            val newCre=mutableListOf<Credentials>()
            service!!.credentials?.forEach {
                val cipher = Cipher.getInstance("AES/GCM/NoPadding")
                cipher.init(Cipher.ENCRYPT_MODE, getKey())
                localEncryptSingle(it)?.let { it1 ->
                    newCre.add(
                        it1
                    )
                }
            }
            if (newCre.isEmpty())
                return null
            val temp= service!!.copy(credentials = newCre,hashData = generateSHA256())
            service=null
            return temp
        }
        return null
    }

    fun localEncryptSingle(credentials: Credentials?):Credentials? {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, getKey())
        if (credentials != null) {
            return credentials.copy(
                data = Base64.getEncoder().encodeToString(cipher.doFinal(credentials.data.toByteArray())),
                iv = Base64.getEncoder().encodeToString(cipher.iv)
            )
        }
        return null
    }


    fun localDecrypt():Service?{
        if(sanityCheck()) {
            val mutableListCredentials=mutableListOf<Credentials>()
            service!!.credentials?.forEach {cre->
                decryptLocalSingleCredentials(cre)?.let { decryptedCredentials ->
                    cre.copy(
                        data = decryptedCredentials.data,
                        iv = decryptedCredentials.iv
                    )
                }?.let { newCred -> mutableListCredentials.add(newCred) }
            }
            if (mutableListCredentials.isEmpty())
                return null
            val temp= service!!.copy(credentials = mutableListCredentials)
            service=null
            return temp
        }
        return null
    }

    fun decryptLocalSingleCredentials(credentials: Credentials?):Credentials?{
        if (credentials!=null) {
            if (credentials.iv==null)
                return credentials
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val spec = GCMParameterSpec(128, Base64.getDecoder().decode(credentials.iv))
            cipher.init(Cipher.DECRYPT_MODE, getKey(),spec)
            return credentials.copy(iv = null,data = cipher.doFinal(
                Base64.getDecoder().decode(
                    credentials.data
                )).toString(Charsets.UTF_8)
            )
        }

        return null
    }


    private fun getKey(): Key {
        val ks: KeyStore = KeyStore.getInstance("AndroidKeyStore").apply {
            load(null)
        }
        val masterKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)
        return ks.getKey(masterKeyAlias,null)
    }

    fun setService(service: Service?): Unit {
        if (service!=null)
            this.service=service
    }
    private fun generateSHA256(): String {
        if(sanityCheck()) {
            var rawData = String()
            service!!.credentials.let {
                it?.forEach { cre ->
                    rawData += cre.data
                    rawData += cre.hint
                }
            }
            val message: ByteArray = rawData.toByteArray()
            val md = MessageDigest.getInstance("SHA-256")
            return Base64.getEncoder().encodeToString(md.digest(message))
        }
        return ""
    }
    private fun sanityCheck(): Boolean {
        if (service==null) {
            return false
        }
        return true
    }


}