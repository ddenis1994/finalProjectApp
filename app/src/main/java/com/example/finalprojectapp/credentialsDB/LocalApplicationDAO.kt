package com.example.finalprojectapp.credentialsDB

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.room.*
import com.example.finalprojectapp.data.model.Credentials
import com.example.finalprojectapp.data.model.DataSet
import com.example.finalprojectapp.data.model.Service
import com.example.finalprojectapp.data.model.relationship.DataSetCredentialsManyToMany
import com.example.finalprojectapp.data.model.relationship.ServiceToDataSet
import com.example.finalprojectapp.crypto.Cryptography
import kotlinx.coroutines.Dispatchers
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


    @Query("SELECT * FROM service_  Where :name like name")
    suspend fun privateGetServiceByNameQuery(name:String): ServiceToDataSet?

    @Transaction
    @Query("Select * from service_")
    fun privateGetService():List<ServiceToDataSet>

    @Query("SELECT * FROM dataSet_  Where :hashData like hashData")
    suspend fun privateFindByHashData(hashData: String): DataSet


    suspend fun publicInsertDataSet(dataSet: DataSet):Pair<Long,List<Long>>{
        return withContext(Dispatchers.IO){
            var hashData=dataSet.hashData
            if (hashData==null){
                hashData= String()
                dataSet.credentials?.forEach {cre->
                    hashData += cre.data
                    hashData += cre.hint
                }
                val message: ByteArray = hashData.toByteArray()
                val md = MessageDigest.getInstance("SHA-256")
                hashData= Base64.getEncoder().encodeToString(md.digest(message))
            }
            var result=privateInsertDataSet(dataSet.copy(hashData = hashData))
            if (result==-1L){
                result=privateFindByHashData(hashData!!).dataSetId
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
            var resultInsert=privateInsertCredentials(cryptography.localEncryptCredentials(credentials.copy(innerHashValue = hashData!!))!!)
            if (resultInsert==-1L){
                resultInsert=privateGetCredentialsByHashData(hashData).credentialsId
            }
            return@withContext resultInsert
        }
    }
    @Query("SELECT * FROM credentials_ Where innerHashValue = :dataSet ")
    suspend fun privateGetCredentialsByHashData(dataSet: String): Credentials
}