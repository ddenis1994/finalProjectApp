package com.example.finalprojectapp.localDB

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import com.example.finalprojectapp.data.model.Credentials
import com.example.finalprojectapp.data.model.LocalServices
import com.example.finalprojectapp.data.model.Service
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Dao
interface LocalCredentialsDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertService(service: Service):Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(password: Credentials):Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(credentials: List<Credentials>)

    @Delete
    suspend fun deleteCredentials(credential : Credentials)

    @Query("select * from passwords")
    fun selectAllPasswords(): LiveData<List<Credentials>>

    @Transaction
    @Query("SELECT * FROM service WHERE serviceId IN (SELECT DISTINCT(serviceId) FROM passwords)")
    fun getAllServiceCredentials(): LiveData<List<LocalServices>>

    @Transaction
    @Query("SELECT * FROM service WHERE serviceId IN (SELECT DISTINCT(serviceId) FROM passwords) and name LIKE :service")
    fun searchServiceCredentials(service:String): LiveData<List<LocalServices>>

    @Transaction
    suspend fun insertServiceCredentials(credentials: List<LocalServices>): MutableLiveData<MutableList<Long>> {
        val final= MutableLiveData<MutableList<Long>>()
        val list= mutableListOf<Long>()
        GlobalScope.launch {
            credentials.forEach {
                val result = insertService(it.service)
                list.add(result)
                it.credentials.forEach { cre ->
                    insert(cre.copy(serviceId = result))
                }

            }
            final.postValue(list)
        }
        return final
    }



}