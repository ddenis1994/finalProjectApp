package com.example.finalprojectapp.credentialsDB

import androidx.room.*
import com.example.finalprojectapp.data.model.Credentials

@Dao
interface CredentialDAO {


    @Update
    fun updateCredentials(it1: Credentials) :Int

    @Query("SELECT * FROM credentials_ Where salt not null ")
    suspend fun getAllEncryptedCredentials(): List<Credentials>

    @Query("SELECT * FROM credentials_")
    suspend fun privateGetAllCredentials(): List<Credentials>

    @Query("Delete from credentials_")
    suspend fun deleteAllCredentials()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun privateInsertCredentials(credentials: Credentials):Long

    @Query("SELECT * FROM credentials_ Where credentialsId = :dataSet ")
    suspend fun privateGetCredentialsID(dataSet: Long): Credentials

    @Query("SELECT * FROM credentials_ Where innerHashValue like :dataSet ")
    suspend fun privateGetCredentialsByHashData(dataSet: String): Credentials?

    @Delete
    fun deleteCredential(vararg dataSet: Credentials)






}