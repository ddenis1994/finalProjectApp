package com.example.finalprojectapp.credentialsDB

import com.example.finalprojectapp.crypto.LocalCryptography
import com.example.finalprojectapp.data.model.Credentials
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class CredentialRepository @Inject constructor(
    private val credentialsDao: CredentialDAO,
    private val localCryptography: LocalCryptography
) {

    fun deleteCredential(credentials: Credentials) = credentialsDao.deleteCredential(credentials)


    suspend fun insertArrayCredentials(listCredentials: List<Credentials>): List<Long> =
        credentialsDao.insertCredentials(*(listCredentials.toTypedArray()))


    suspend fun publicInsertCredentials(credentials: Credentials): Long {
        return withContext(Dispatchers.IO) {
            if (credentials.salt != null) return@withContext -1L
            val encryptedCredentials = localCryptography.encrypt(credentials)
            var resultInsert: Long = -1L
            encryptedCredentials?.let {
                resultInsert = credentialsDao.insertCredentials(it)[0]
                if (resultInsert == -1L)
                    resultInsert =
                        it.innerHashValue?.let { hash ->
                            credentialsDao.getCredentialsByHashData(
                                hash
                            )?.credentialsId
                        } ?: -1L
            }
            return@withContext resultInsert
        }
    }


    suspend fun publicGetCredentialsID(dataSet: Long): Credentials? {
        val result = credentialsDao.getCredentialsByID(dataSet)
        return localCryptography.decryption(result)

    }


    suspend fun deleteAllCredentials() =
        credentialsDao.deleteAllCredentials()


    suspend fun privateGetAllCredentials(): List<Credentials> =
        credentialsDao.getAllCredentials()


}