package com.example.finalprojectapp.localDB

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.finalprojectapp.data.model.LocalCredentials
import com.example.finalprojectapp.data.model.LocalServiceCredentials

@Dao
interface LocalCredentialsDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(password: LocalCredentials):Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(credentials: List<LocalCredentials>)

    @Delete
    suspend fun deleteCredentials(credential : LocalCredentials)

    @Query("select * from passwords")
    fun selectAllPasswords(): LiveData<List<LocalCredentials>>

    @Transaction
    @Query("SELECT * FROM services WHERE serviceId IN (SELECT DISTINCT(serviceId) FROM passwords)")
    fun getAllServiceCredentials(): LiveData<List<LocalServiceCredentials>>

    @Transaction
    @Query("SELECT * FROM services WHERE serviceId IN (SELECT DISTINCT(serviceId) FROM passwords) and serviceName LIKE :service")
    fun searchServiceCredentials(service:String): LiveData<List<LocalServiceCredentials>>

}