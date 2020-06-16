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

     internal fun deleteCredential(credentials: Credentials) = credentialsDao.deleteCredential(credentials)


    suspend fun insertCredentials(vararg listCredentials: Credentials): List<Long> =
        withContext(Dispatchers.IO) {
            return@withContext listCredentials.map { insertSingleCredential(it) }
        }


    private suspend fun insertSingleCredential(credentials: Credentials): Long {
        if (credentials.salt != null) return -1L
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
        return resultInsert
    }


    suspend fun publicGetCredentialsID(dataSet: Long): Credentials? =
        localCryptography.decryption(credentialsDao.getCredentialsByID(dataSet))


    suspend fun deleteAllCredentials() =
        credentialsDao.deleteAllCredentials()


    fun deleteCredentialByDataSetID(dataSetId: Long) {
        credentialsDao.deleteCredentialByDataSetID(dataSetId)
    }

    fun getCredentialByDataSetID(dataSetId: Long)=credentialsDao.publicGetAllCredentialsByDataSetID(dataSetId)
    fun publicGetAllHashCredentials()=credentialsDao.publicGetAllHashCredentials()


}