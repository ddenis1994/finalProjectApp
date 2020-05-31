package com.example.finalprojectapp.crypto

import android.content.SharedPreferences
import android.security.keystore.WrappedKeyEntry
import com.example.finalprojectapp.credentialsDB.ServiceRepository
import javax.inject.Inject

class MasterPassword @Inject constructor(
    private val settings: SharedPreferences,
    private val serviceRepository: ServiceRepository,
    private val hashBuilder: HashBuilder
) {

    private val mainPassword: String? =
        hashBuilder.makeHash(settings.getString("mainPassword", ""))


    fun setPassword(newMainPassword: String): Boolean {
        val hash = hashBuilder.makeHash(newMainPassword)
        serviceRepository.updateRemotePassword(hash)
        settings.edit().putString("mainPassword", newMainPassword).apply()

        return true
    }

    fun checkForLocalPassword(password: String): Boolean {
        val hash = hashBuilder.makeHash(password)
        return mainPassword == hash
    }

}