package com.example.finalprojectapp.localDB

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.liveData
import androidx.room.*
import com.example.finalprojectapp.data.model.Credentials
import com.example.finalprojectapp.data.model.Service
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@Dao
interface CredentialsDAO {
    data class LocalServices(
        @Embedded val service: Service,
        @Relation(
            parentColumn = "serviceId",
            entityColumn = "serviceId"
        )
        var credentials: List<Credentials>
    )

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertService(service: Service): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(password: Credentials): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(credentials: List<Credentials>)

    @Delete
    fun deleteCredentials(credential: Credentials)

    @Query("select * from passwords")
    fun selectAllPasswords(): LiveData<List<Credentials>>

    @Query("select * from passwords Where iv IS NOT NULL")
    suspend fun getAllEncryptedCredentials(): List<Credentials>

    @Transaction
    @Query("SELECT * FROM service WHERE serviceId IN (SELECT DISTINCT(serviceId) FROM passwords)")
    fun getAllServiceCredentialsPrivate(): LiveData<List<LocalServices>>

    fun getAllServiceCredentialsPublic(): LiveData<List<Service>> {
            val result = getAllServiceCredentialsPrivate()

            val changeDetection=MutableLiveData<List<Service>>()
            val test=Observer<List<LocalServices>>{
                val list = mutableListOf<Service>()
                it.forEach {service->
                    list.add(Service(service))
                }
                changeDetection.postValue(list.toList())

            }
            result.observeForever (test)
        return changeDetection

    }


    @Transaction
    @Query("SELECT * FROM service WHERE serviceId IN (SELECT DISTINCT(serviceId) FROM passwords) and name LIKE :service")
    fun searchServiceCredentialsPrivate(service: String): LocalServices?

    fun searchServiceCredentialsPublic(service: String): LiveData<Service?> {
        return liveData {
            withContext(Dispatchers.IO) {
                val result = searchServiceCredentialsPrivate(service)
                if (result == null)
                    emit(null)
                else
                    emit(Service(result))
            }
        }

    }
    @Delete
    fun deleteService(serviceName: Service)

    suspend fun insertSingleServiceCredentials(credentials: Service) {
        withContext(Dispatchers.IO) {
            searchServiceCredentialsPrivate(credentials.name)?.let {ser->
                deleteService(Service(ser))
                Service(ser).credentials?.forEach {
                    deleteCredentials(it)
                }
            }

        val result = insertService(credentials)
        credentials.credentials?.forEach { cre ->
            insert(cre.copy(serviceId = result))
        }
    }
    }


    suspend fun deleteFullService(credentials: Service){
            withContext(Dispatchers.IO) {
                val result = searchServiceCredentialsPrivate(credentials.name)?.let {
                    Service(it)
                }
                if (result != null) {
                    deleteService(result)
                    result.credentials?.forEach {
                        deleteCredentials(it)
                    }
            }
        }
    }


}