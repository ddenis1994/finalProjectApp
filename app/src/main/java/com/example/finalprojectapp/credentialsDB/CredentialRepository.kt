package com.example.finalprojectapp.credentialsDB

import android.content.Context
import com.example.finalprojectapp.crypto.Cryptography
import com.example.finalprojectapp.data.model.Credentials

class CredentialRepository private constructor(
    context: Context
){

    private val credentialsDao=CredentialsDataBase.getDatabase(context).credentialDAO()

    fun deleteCredential(credentials: Credentials)=credentialsDao.deleteCredential(credentials)


    suspend fun publicInsertArrayCredentials(listCredentials: List<Credentials>): List<Long> {
        val list= mutableListOf<Long>()
        listCredentials.forEach {
            list.add(publicInsertCredentials(it))
        }
        return list
    }


    suspend fun publicGetCredentialsID(dataSet: Long): Credentials {
        val result=credentialsDao.privateGetCredentialsID(dataSet)
        val cryptography= Cryptography(null)
        return if (cryptography.localDecryptCredentials(result)==null)
            Credentials()
        else
            cryptography.localDecryptCredentials(result)!!
    }

    suspend fun publicInsertCredentials(credentials: Credentials):Long{
        return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            val cryptography = Cryptography(null)
            var hashData = credentials.innerHashValue
            if (hashData == null) {
                val message: ByteArray = (credentials.data + credentials.hint).toByteArray()
                val md = java.security.MessageDigest.getInstance("SHA-256")
                hashData = java.util.Base64.getEncoder().encodeToString(md.digest(message))
            }

            var resultInsert = if (credentials.salt != null)
                credentialsDao.privateInsertCredentials(credentials.copy(innerHashValue = hashData!!))
            else
                credentialsDao.privateInsertCredentials(
                    cryptography.localEncryptCredentials(
                        credentials.copy(
                            innerHashValue = hashData!!
                        )
                    )!!
                )
            if (resultInsert == -1L) {
                resultInsert = credentialsDao.privateGetCredentialsByHashData(hashData).credentialsId
            }
            return@withContext resultInsert
        }
    }

    suspend fun deleteAllCredentials() {
        credentialsDao.deleteAllCredentials()
    }


    companion object {
        // For Singleton instantiation
        @Volatile
        private var instance: CredentialRepository? = null
        fun getInstance(context: Context) =
            instance ?: synchronized(this) {
                instance
                    ?: CredentialRepository(
                        context
                    )
                        .also { instance = it }
            }
    }

}