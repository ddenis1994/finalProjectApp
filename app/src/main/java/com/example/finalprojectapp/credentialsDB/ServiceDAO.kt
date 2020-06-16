package com.example.finalprojectapp.credentialsDB

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.finalprojectapp.data.model.Service
import com.example.finalprojectapp.data.model.adpters.LayoutDashBoardRepeatedPassword
import com.example.finalprojectapp.data.model.adpters.LayoutServiceView
import com.example.finalprojectapp.data.model.relationship.ServiceWithDataSets

@Dao
interface ServiceDAO {


    @Query("Delete from service_")
    suspend fun deleteAllService()

    @Transaction
    @Query("select s.name serviceName,d.dataSetName dataSetName from  dataSet_ d ,service_ s,credentials_ c where :credentialID=c.credentialsId and d.serviceId = s.serviceId and d.dataSetId= c.credentialDataSetId " )
    suspend fun findServiceAndDataSetsAndCredentials(credentialID: Long): List<LayoutDashBoardRepeatedPassword>

    @Query("select count(*) from  service_ ")
    fun publicGetNumOfServices():LiveData<Int>

    @Query("Select s.name,s.serviceId from service_ s")
    fun publicGetAllServiceName(): LiveData<List<LayoutServiceView>>

    @Query("select * from dataSet_ d , service_ s where d.dataSetId=:dataSetId and s.serviceId = d.serviceId")
    fun getServiceByDataSetId(dataSetId: Long):ServiceWithDataSets?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun privateInsertService(dataSet: Service):Long

    @Transaction
    @Query("SELECT * FROM service_ ")
    suspend fun privateGetAllService(): List<ServiceWithDataSets>

    @Transaction
    @Query("SELECT * FROM service_  Where :name like name")
    suspend fun getServiceByName(name:String): ServiceWithDataSets?


    @Delete
    suspend fun deleteService(vararg service: Service)

    @Update
    fun updateService(target: Service)


}