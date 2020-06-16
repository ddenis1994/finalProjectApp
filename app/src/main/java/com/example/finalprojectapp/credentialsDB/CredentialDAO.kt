package com.example.finalprojectapp.credentialsDB

import androidx.room.*
import com.example.finalprojectapp.data.model.Credentials

@Dao
interface CredentialDAO {


    @Update
    fun updateCredentials(it1: Credentials) :Int

    @Query("SELECT * FROM credentials_ Where salt not null ")
    suspend fun getCredentialsWithSalt(): List<Credentials>

    @Query("SELECT * FROM credentials_")
    suspend fun getAllCredentials(): List<Credentials>

    @Query("Delete from credentials_")
    suspend fun deleteAllCredentials()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCredentials(vararg credentials: Credentials):List<Long>


    @Query("SELECT * FROM credentials_ Where credentialsId = :id")
    suspend fun getCredentialsByID(id: Long): Credentials?

    @Query("SELECT * FROM credentials_ Where innerHashValue like :hash ")
    suspend fun getCredentialsByHashData(hash: String): Credentials?

    @Delete
    fun deleteCredential(vararg dataSet: Credentials)


    @Query("Delete from credentials_ where credentialDataSetId = :dataSetId")
    fun deleteCredentialByDataSetID(dataSetId: Long)

}