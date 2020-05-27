package com.example.finalprojectapp.credentialsDB

import android.content.Context
import com.example.finalprojectapp.DaggerApplicationComponent
import com.example.finalprojectapp.crypto.LocalCryptography
import com.example.finalprojectapp.data.model.Credentials

class CredentialRepository private constructor(
    context: Context
){

    private val credentialsDao=LocalDataBase.getDatabase(context).credentialDAO()

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
        val applicationComponent=DaggerApplicationComponent.create()
        val cryptography:LocalCryptography=applicationComponent.getLocalLocalCryptography()
        return if (cryptography.localDecryption(result)==null)
            Credentials()
        else
            cryptography.localDecryption(result)!!
    }

    suspend fun publicInsertCredentials(credentials: Credentials):Long{
        return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            val applicationComponent=DaggerApplicationComponent.create()
            val cryptography:LocalCryptography=applicationComponent.getLocalLocalCryptography()
            //val cryptography = LocalCryptography(null)
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
                    cryptography.localEncrypt(
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

    suspend fun privateGetAllCredentials(): List<Credentials> {
        return credentialsDao.privateGetAllCredentials()
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