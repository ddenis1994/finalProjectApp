package com.example.finalprojectapp.localDB

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.finalprojectapp.data.model.LocalService

@Dao
interface LocalServiceDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(service: LocalService):Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(services: List<LocalService>):List<Long>

    @Query("SELECT * FROM services ORDER BY serviceName")
    fun getAllServices(): LiveData<List<LocalService>>

    @Query("SELECT * FROM services WHERE serviceName = :serviceName")
    fun getService(serviceName: String): LiveData<List<LocalService>>

    @Query("SELECT EXISTS(SELECT 1 FROM services WHERE serviceName LIKE :serviceName LIMIT 1)")
    fun isExists(serviceName: String):LiveData<Boolean>


}