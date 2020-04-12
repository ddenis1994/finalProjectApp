package com.example.finalprojectapp.localDB

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.room.*
import com.example.finalprojectapp.data.model.Credentials

import com.example.finalprojectapp.data.model.Service
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext



@Dao
interface LocalCredentialsDAO {
    data class LocalServices (
        @Embedded val service: Service,
        @Relation(
            parentColumn = "serviceId",
            entityColumn = "serviceId"
        )
        var credentials: List<Credentials>
    )

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertService(service: Service): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(password: Credentials): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(credentials: List<Credentials>)

    @Delete
    suspend fun deleteCredentials(credential: Credentials)

    @Query("select * from passwords")
    fun selectAllPasswords(): LiveData<List<Credentials>>

    @Query("select * from passwords Where iv IS NOT NULL")
    suspend fun getAllEncryptedCredentials(): List<Credentials>

    @Transaction
    @Query("SELECT * FROM service WHERE serviceId IN (SELECT DISTINCT(serviceId) FROM passwords)")
    fun getAllServiceCredentialsPrivate(): List<LocalServices>

    suspend fun getAllServiceCredentialsPublic(): LiveData<List<Service>> {
        return liveData {
            withContext<Unit>(Dispatchers.IO) {
                val result = getAllServiceCredentialsPrivate()
                val list = mutableListOf<Service>()
                result.forEach { localService ->
                    list.add(Service(localService))
                }
                emit(list.toList())
            }
        }

    }


    @Transaction
    @Query("SELECT * FROM service WHERE serviceId IN (SELECT DISTINCT(serviceId) FROM passwords) and name LIKE :service")
    fun searchServiceCredentialsPrivate(service: String): List<LocalServices>

    suspend fun searchServiceCredentialsPublic(service: String): LiveData<List<Service>> {
        return liveData {
            withContext<Unit>(Dispatchers.IO) {
                val result = searchServiceCredentialsPrivate(service)
                val list = mutableListOf<Service>()
                result.forEach { localService ->
                    list.add(Service(localService))
                }
                emit(list.toList())
            }
        }

    }


    suspend fun insertServiceCredentials(credentials: List<Service>): LiveData<List<Long>> {
        return liveData {
                val list = mutableListOf<Long>()
                credentials.forEach {
                    val result = insertService(it)
                    list.add(result)
                    it.credentials?.forEach { cre ->
                        insert(cre.copy(serviceId = result))
                    }
                }
                emit(list.toList())
            }
    }
}