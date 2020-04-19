package com.example.finalprojectapp.credentialsDB

import androidx.room.*
import com.example.finalprojectapp.credentialsDB.model.Credentials
import com.example.finalprojectapp.credentialsDB.model.DataSet
import com.example.finalprojectapp.credentialsDB.model.Service
import com.example.finalprojectapp.credentialsDB.model.relationship.DataSetCredentialsManyToMany
import com.example.finalprojectapp.credentialsDB.model.relationship.ServiceToDataSet
import com.example.finalprojectapp.crypto.Cryptography
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.util.*


@Dao
interface LocalServiceDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun privateInsertCredentials(credentials: Credentials):Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun privateInsertCredentials(dataSetCredentialsManyToMany: DataSetCredentialsManyToMany):Long


    @Query("SELECT * FROM credentials_")
    suspend fun privateGetAllCredentials(): List<Credentials>


    @Query("SELECT * FROM credentials_ Where innerHashValue = :dataSet ")
    suspend fun privateGetCredentialsByHashData(dataSet: String): Credentials

    @Query("SELECT * FROM credentials_ Where credentialsId = :dataSet ")
    suspend fun privateGetCredentialsID(dataSet: Long): Credentials


    suspend fun publicGetCredentialsID(dataSet: Long): Credentials{
        val result=privateGetCredentialsID(dataSet)
        val cryptography=Cryptography(null)
        return cryptography.localDecryptCredentials(result)!!
    }




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

    suspend fun publicInsertArrayCredentials(listCredentials: List<Credentials>): List<Long> {
        val list= mutableListOf<Long>()
        listCredentials.forEach {
            list.add(publicInsertCredentials(it))
        }
        return list
    }


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun privateInsertDataSet(dataSet: DataSet):Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun privateInsertService(dataSet: Service):Long

    suspend fun publicInsertService(service: Service):Pair<Long,List<Pair<Long,List<Long>>>>{
        var result=privateInsertService(service)
        if (result==-1L){
            result=privateGetServiceByName(service.name)!!.service.serviceId
        }
        val list= mutableListOf<Pair<Long,List<Long>>>()
        service.dataSets?.forEach {
            list.add(publicInsertDataSet(it.copy(serviceId = result)))
        }
        return Pair(result,list)
    }

    suspend fun publicGetServiceByName(string: String):Service{
        val service= privateGetServiceByName(string) ?: return Service()

        val list= mutableListOf<DataSet>()
        service.dataSets.forEach {
            list.add(getDataSetByID(it.dataSetId))
        }
        return service.service.copy(dataSets = list)
    }

    @Query("SELECT * FROM service_ ")
    suspend fun privateGetAllService(): List<ServiceToDataSet>

    suspend fun publicGetAllService():List<Service>{
        val service= privateGetAllService()
        val servicesList= mutableListOf<Service>()
        service.forEach {ser->
            val list= mutableListOf<DataSet>()
            ser.dataSets.forEach {
                list.add(getDataSetByID(it.dataSetId))
            }
            servicesList.add(ser.service.copy(dataSets = list))

        }
        return servicesList
    }

    @Query("SELECT * FROM service_  Where :name like name")
    suspend fun privateGetServiceByName(name:String): ServiceToDataSet?





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
                hashData=Base64.getEncoder().encodeToString(md.digest(message))
            }
            var result=privateInsertDataSet(dataSet.copy(hashData = hashData))
            if (result==-1L){
                result=privateGetDataSet(hashData!!).dataSetId
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

    //function to get all data by hash value for the dataSet
    suspend fun getDataSetByHash(hash:String):DataSet{

        val dataSet=privateGetDataSetByHash(hash)
        val allData=privateGetDataSetToCredentials(dataSet.dataSetId)
        val listCredentials= mutableListOf<Credentials>()
        allData.forEach {
            listCredentials.add(publicGetCredentialsID(it.credentialsId))
        }
        return dataSet.copy(credentials = listCredentials)
    }

    //function to get all data by id value for the dataSet
    suspend fun getDataSetByID(id:Long):DataSet{

        val dataSet=privateGetDataSetByDataSetID(id)
        val allData=privateGetDataSetToCredentials(dataSet.dataSetId)
        val listCredentials= mutableListOf<Credentials>()
        allData.forEach {
            listCredentials.add(publicGetCredentialsID(it.credentialsId))
        }
        return dataSet.copy(credentials = listCredentials)
    }

    @Query("SELECT * FROM dataSet_ Where :hashData = hashData")
    suspend fun privateGetDataSetByHash(hashData:String): DataSet

    @Query("SELECT * FROM dataSet_ Where :hashData = dataSetId")
    suspend fun privateGetDataSetByDataSetID(hashData:Long): DataSet

    @Query("SELECT * FROM dataSetCredentialsManyToMany Where dataSetId =:num ")
    suspend fun privateGetDataSetToCredentials(num:Long): List<DataSetCredentialsManyToMany>

    @Transaction
    @Query("SELECT * FROM dataSet_ Where :hashData = hashData")
    suspend fun privateGetDataSet(hashData:String): DataSet




}