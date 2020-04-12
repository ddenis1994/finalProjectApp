package com.example.finalprojectapp.localDB

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.finalprojectapp.data.model.Service


@Dao
interface ServiceDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(service: Service):Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(services: List<Service>):List<Long>

    @Query("SELECT * FROM service ORDER BY name")
    fun getAllServices(): LiveData<List<Service>>

    @Query("SELECT * FROM service WHERE name = :serviceName")
    fun getService(serviceName: String): LiveData<List<Service>>

    @Query("SELECT EXISTS(SELECT 1 FROM service WHERE name LIKE :serviceName LIMIT 1)")
    fun isExists(serviceName: String):LiveData<Boolean>
}