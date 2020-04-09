package com.example.finalprojectapp.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class SingleEncryptedSharedPreferences {
    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    private var innerInstance:SharedPreferences? = null
    fun getSharedPreference(context: Context): SharedPreferences {
        val tempInstance=innerInstance
        if(tempInstance != null)
            return tempInstance
        synchronized(this){
            val instance=EncryptedSharedPreferences.create(
                    "PreferencesFilename",
                    masterKeyAlias,
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
            innerInstance=instance
            return instance
        }

    }

}