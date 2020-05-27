package com.example.finalprojectapp.credentialsDB

import com.example.finalprojectapp.crypto.LocalCryptography
import com.example.finalprojectapp.data.model.Credentials
import javax.inject.Inject


class CredentialRepository @Inject constructor(
    private val credentialsDao: CredentialDAO,
    private val localCryptography: LocalCryptography
) {

    fun deleteCredential(credentials: Credentials) = credentialsDao.deleteCredential(credentials)


    suspend fun publicInsertArrayCredentials(listCredentials: List<Credentials>): List<Long> {
        val list = mutableListOf<Long>()
        listCredentials.forEach {
            publicInsertCredentials(it)?.let { it1 -> list.add(it1) }
        }
        return list
    }

    suspend fun publicInsertCredentials(credentials: Credentials): Long? {
        return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            if (credentials.salt != null) return@withContext null
            val encryptedCredentials = localCryptography.encrypt(credentials)
            var resultInsert: Long? = null
            encryptedCredentials?.let {
                resultInsert = credentialsDao.privateInsertCredentials(it)
                if (resultInsert == -1L)
                    resultInsert =
                        credentialsDao.privateGetCredentialsByHashData(it.innerHashValue!!).credentialsId
            }
            return@withContext resultInsert
        }
    }


    suspend fun publicGetCredentialsID(dataSet: Long): Credentials? {
        val result = credentialsDao.privateGetCredentialsID(dataSet)
        return if (localCryptography.localDecryption(result) == null)
            null
        else
            localCryptography.localDecryption(result)!!
    }


    suspend fun deleteAllCredentials() {
        credentialsDao.deleteAllCredentials()
    }

    suspend fun privateGetAllCredentials(): List<Credentials> {
        return credentialsDao.privateGetAllCredentials()
    }

}