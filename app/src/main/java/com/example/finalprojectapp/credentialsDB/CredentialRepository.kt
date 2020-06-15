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
            publicInsertCredentials(it).let { it1 -> list.add(it1) }
        }
        return list
    }

    suspend fun publicInsertCredentials(credentials: Credentials): Long {
        return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            if (credentials.salt != null) return@withContext -1L
            val encryptedCredentials = localCryptography.encrypt(credentials)
            var resultInsert: Long =-1L
            encryptedCredentials?.let {
                resultInsert = credentialsDao.privateInsertCredentials(it)[0]
                if (resultInsert == -1L)
                    resultInsert =
                        it.innerHashValue?.let { hash ->
                            credentialsDao.getCredentialsByHashData(
                                hash
                            )?.credentialsId
                        }?: -1L
            }
            return@withContext resultInsert
        }
    }


    suspend fun publicGetCredentialsID(dataSet: Long): Credentials? {
        val result = credentialsDao.getCredentialsByID(dataSet)
        return if (localCryptography.decryption(result) == null)
            null
        else
            localCryptography.decryption(result)!!
    }


    suspend fun deleteAllCredentials() {
        credentialsDao.deleteAllCredentials()
    }

    suspend fun privateGetAllCredentials(): List<Credentials> {
        return credentialsDao.getAllCredentials()
    }

}