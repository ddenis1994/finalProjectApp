package com.example.finalprojectapp.credentialsDB

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.finalprojectapp.data.model.Service
import com.example.finalprojectapp.data.model.adpters.LayoutDashBoardRepeatedPassword
import com.example.finalprojectapp.data.model.adpters.LayoutServiceView
import com.example.finalprojectapp.data.model.relationship.ServiceToDataSet

@Dao
interface ServiceDAO {

    @Query("SELECT d.dataSetId FROM service_ s, dataSet_ d,credentials_ c Where :serviceName like s.name AND d.serviceId = s.serviceId AND c.credentialsId==:credentialsID ")
    suspend fun privateGetUnionServiceNameAndCredentialsHash(serviceName:String,credentialsID: Long): Long?

    @Transaction
    @Query("SELECT * FROM service_  Where :name like name")
    suspend fun privateGetServiceByName(name:String): Service?

    @Query("Delete from service_")
    suspend fun deleteAllService()

    // TODO: 16/06/2020 fix this 
    @Query("select s.name serviceName,d.dataSetName dataSetName from  dataSet_ d ,service_ s where d.serviceId = s.serviceId " )
    suspend fun publicFindServiceAndDataSet(/*credentialID: Long*/): List<LayoutDashBoardRepeatedPassword>

    @Query("select count(*) from  service_ ")
    fun publicGetNumOfServices():LiveData<Int>

    @Query("Select s.name,s.serviceId from service_ s")
    fun publicGetAllServiceName(): LiveData<List<LayoutServiceView>>

    @Query("select s.name from dataSet_ d , service_ s where d.dataSetId=:dataSetId and s.serviceId = d.serviceId")
    fun getServiceByDataSetId(dataSetId: Long):String?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun privateInsertService(dataSet: Service):Long

    @Transaction
    @Query("SELECT * FROM service_ ")
    suspend fun privateGetAllService(): List<ServiceToDataSet>

    @Transaction
    @Query("SELECT * FROM service_  Where :name like name")
    suspend fun privateGetServiceByNameInner(name:String): ServiceToDataSet?

    @Transaction
    @Query("SELECT * FROM service_  Where :name like name")
    suspend fun privateGetServiceByNameQuery(name:String): ServiceToDataSet?

    @Delete
    suspend fun privateDeleteService(vararg service: Service)

    @Query("delete from service_ where serviceId=:id")
    suspend fun privateDeleteServiceByID(id:Long)

    // TODO: 16/06/2020 remove in production
    @Transaction
    @Query("Select * from service_")
    fun privateGetService():List<ServiceToDataSet>

}