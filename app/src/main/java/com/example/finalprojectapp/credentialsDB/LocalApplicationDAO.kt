package com.example.finalprojectapp.credentialsDB

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.room.*
import com.example.finalprojectapp.data.model.Credentials
import com.example.finalprojectapp.data.model.DataSet
import com.example.finalprojectapp.data.model.Service
import com.example.finalprojectapp.data.model.relationship.DataSetCredentialsManyToMany
import com.example.finalprojectapp.data.model.relationship.ServiceToDataSet
import com.example.finalprojectapp.crypto.Cryptography
import com.example.finalprojectapp.data.model.adpters.LayoutServiceView
import com.example.finalprojectapp.data.model.adpters.LayoutDataSetView
import com.example.finalprojectapp.data.model.adpters.LayoutCredentialView
import com.example.finalprojectapp.data.model.adpters.LayoutDashBoardRepeatedPassword
import com.example.finalprojectapp.ui.dashboard.DashboardViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.util.*

@Dao
interface LocalApplicationDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun privateInsertCredentials(credentials: Credentials):Long

    @Query("SELECT * FROM credentials_ Where credentialsId = :dataSet ")
    suspend fun privateGetCredentialsID(dataSet: Long): Credentials

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun privateInsertDataSet(dataSet: DataSet):Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun privateInsertService(dataSet: Service):Long

    @Query("SELECT * FROM service_ ")
    suspend fun privateGetAllService(): List<ServiceToDataSet>

    @Transaction
    @Query("SELECT * FROM service_  Where :name like name")
    suspend fun privateGetServiceByNameInner(name:String): ServiceToDataSet?

    suspend fun publicGetCredentialsID(dataSet: Long): Credentials {
        val result=privateGetCredentialsID(dataSet)
        val cryptography=Cryptography(null)
        return cryptography.localDecryptCredentials(result)!!
    }



    fun publicGetAllService():LiveData<List<Service>>{
        return liveData {
            withContext(Dispatchers.IO) {
                val service = privateGetAllService()
                val servicesList = mutableListOf<Service>()
                service.forEach { ser ->
                    val list = mutableListOf<DataSet>()
                    ser.dataSets.forEach {
                        list.add(getDataSetByID(it.dataSetId))
                    }
                    servicesList.add(ser.service.copy(dataSets = list))
                }
                emit(servicesList.toList())
            }
        }

    }


    fun publicGetServiceByNameLive(string: String):LiveData<Service?>{
        return liveData {
            withContext(Dispatchers.IO) {
                emit(privateGetServiceByName(string))
            }
        }
    }

    suspend fun privateGetServiceByName(string: String): Service?{
        val service = privateGetServiceByNameQuery(string) ?: return null
        val list = mutableListOf<DataSet>()
        service.dataSets.forEach {
            list.add(getDataSetByID(it.dataSetId))
        }
        return service.service.copy(dataSets = list)
    }

    suspend fun getDataSetByID(id:Long): DataSet {
        val dataSet=privateGetDataSetByDataSetID(id)
        val allData=privateGetDataSetToCredentials(dataSet.dataSetId)
        val listCredentials= mutableListOf<Credentials>()
        allData.forEach {
            listCredentials.add(publicGetCredentialsID(it.credentialsId))
        }
        return dataSet.copy(credentials = listCredentials)
    }

    @Query("SELECT * FROM dataSet_ Where :hashData = dataSetId")
    suspend fun privateGetDataSetByDataSetID(hashData:Long): DataSet

    @Query("SELECT * FROM dataSetCredentialsManyToMany Where dataSetId =:num ")
    suspend fun privateGetDataSetToCredentials(num:Long): List<DataSetCredentialsManyToMany>


    fun deleteFullService(service: String):LiveData<Boolean>{
        return liveData {
            withContext(Dispatchers.IO) {
                val serviceLocal = privateGetServiceByName(service)
                if (serviceLocal==null) {
                    emit(false)
                    return@withContext
                }
                serviceLocal.dataSets?.forEach {
                    privateDeleteDataSet(it)
                }
                privateDeleteService(serviceLocal)
                emit(true)
            }
        }
    }

    suspend fun privateDeleteDataSet(dataSet: DataSet){
        val rel=findAllRelationshipToDataSet(dataSet.dataSetId)
        rel.forEach {
            deleteDataSetRelationship(it)
        }
        deleteDataSet(dataSet)
    }

    @Query("SELECT * FROM dataSetCredentialsManyToMany  Where :dataSetId like DataSetCredentialsManyToManyID")
    fun findAllRelationshipToDataSet(vararg dataSetId:Long):List<DataSetCredentialsManyToMany>

    @Delete
    fun deleteDataSetRelationship(vararg dataSetCredentialsManyToMany: DataSetCredentialsManyToMany)

    @Delete
    fun deleteDataSet(vararg dataSet: DataSet)

    @Delete
    fun privateDeleteService(vararg service: Service)

    @Transaction
    @Query("SELECT * FROM service_  Where :name like name")
    suspend fun privateGetServiceByNameQuery(name:String): ServiceToDataSet?

    @Transaction
    @Query("Select * from service_")
    fun privateGetService():List<ServiceToDataSet>

    @Query("SELECT * FROM dataSet_  Where :hashData like hashData")
    suspend fun privateFindByHashData(hashData: String): DataSet

    @Query("SELECT * FROM dataSet_  Where :hashData like hashData And serviceId = :service")
    suspend fun privateFindByHashDataAndServiceId(hashData: String,service: Long): DataSet?


    suspend fun publicInsertDataSet(
        dataSet: DataSet,
        serviceName: String
    ):Pair<Long,List<Long>>?{
        return withContext(Dispatchers.IO){
            val hashData: String? = dataSet.hashData ?: return@withContext null
            val service=privateGetServiceByName(serviceName) ?: return@withContext null

            if(privateFindByHashDataAndServiceId(hashData!!,service.serviceId)!=null) {
                return@withContext null
            }

            var result=privateInsertDataSet(dataSet.copy(hashData = hashData,serviceId = service.serviceId))
            if (result==-1L){
                result=privateFindByHashData(hashData).dataSetId
            }
            val creList= mutableListOf<Long>()
            dataSet.credentials?.forEach {
                val insertResult=publicInsertCredentials(it)
                privateInsertCredentials(DataSetCredentialsManyToMany(dataSetId=result,credentialsId = insertResult))
                creList.add(insertResult)
            }
            return@withContext Pair(result,creList)
        }
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun privateInsertCredentials(dataSetCredentialsManyToMany: DataSetCredentialsManyToMany):Long


    suspend fun publicInsertCredentials(credentials: Credentials):Long{
        return withContext(Dispatchers.IO){
            val cryptography=Cryptography(null)
            var hashData= credentials.innerHashValue
            if (hashData==null){
                val message: ByteArray = (credentials.data+credentials.hint).toByteArray()
                val md = MessageDigest.getInstance("SHA-256")
                hashData= Base64.getEncoder().encodeToString(md.digest(message))
            }

            var resultInsert = if(credentials.salt!=null)
                privateInsertCredentials(credentials.copy(innerHashValue = hashData!!))
            else
                privateInsertCredentials(cryptography.localEncryptCredentials(credentials.copy(innerHashValue = hashData!!))!!)
            if (resultInsert==-1L){
                resultInsert=privateGetCredentialsByHashData(hashData).credentialsId
            }
            return@withContext resultInsert
        }
    }
    @Query("SELECT * FROM credentials_ Where innerHashValue = :dataSet ")
    suspend fun privateGetCredentialsByHashData(dataSet: String): Credentials


    fun publicInsertService(toObject: Service):LiveData<Pair<Long,List<Pair<Long,List<Long>>>>> {
        return liveData {
            withContext(Dispatchers.IO) {
                emit(privateInsertServiceInner(toObject))
            }
        }
    }

    suspend fun privateInsertServiceInner(service: Service):Pair<Long,List<Pair<Long,List<Long>>>>{
        var result=privateInsertService(service)
        if (result==-1L){
            result= privateGetServiceByName(service.name)!!.serviceId
        }
        val list= mutableListOf<Pair<Long,List<Long>>>()
        service.dataSets?.forEach {
            publicInsertDataSet(it.copy(serviceId = result),service.name)?.let { it1 -> list.add(it1) }
        }
        return Pair(result,list)
    }



    @Query("Select s.name,s.serviceId from service_ s")
    fun publicGetAllServiceName():LiveData<List<LayoutServiceView>>

    @Query("Select c.iv,c.data,c.hint from dataSetCredentialsManyToMany r,credentials_ c where r.dataSetId=:dataSetId and r.credentialsId = c.credentialsId")
    fun publicGetAllCredentialsByDataSetID(dataSetId:Long):LiveData<List<LayoutCredentialView>>


    @Query("Select d.dataSetName,d.dataSetId from dataSet_ d where d.serviceId=:serviceId ")
    fun publicGetAllDataSetsByServiceId(serviceId:Long):LiveData<List<LayoutDataSetView>>

    @Query("DELETE FROM dataSetCredentialsManyToMany  WHERE dataSetId=:dataSetId")
    fun deleteFromRelationship(dataSetId: Long): Int

    @Transaction
    suspend fun deleteDataSetById(dataSetId: Long) {
        deleteDataSet(DataSet().copy(dataSetId = dataSetId))
        deleteFromRelationship(dataSetId)
    }


    @Query("select s.name from dataSet_ d , service_ s where d.dataSetId=:dataSetId and s.serviceId = d.serviceId")
    fun getServiceByDataSetId(dataSetId: Long):String?

    @Query("select count(*) from  service_ ")
    fun publicGetNumOfServices():LiveData<Int>

    @Transaction
    @Query("select  r.credentialsId,c.innerHashValue hash, r.dataSetId dataSet from  credentials_ c,dataSetCredentialsManyToMany r where r.credentialsId=c.credentialsId and c.hint like '%Password%' or '%password%'   ")
    fun publicGetAllHashCredentials():LiveData<List<DashboardViewModel.HashAndId>>

    @Transaction
    @Query("select s.name serviceName,d.dataSetName dataSetName from dataSetCredentialsManyToMany r, dataSet_ d ,service_ s where d.serviceId = s.serviceId and d.dataSetId = r.dataSetId and r.credentialsId = :credentialID" )
    fun publicFindServiceAndDataSet(credentialID: Long): LiveData<List<LayoutDashBoardRepeatedPassword>>


}