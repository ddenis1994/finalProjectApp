package com.example.finalprojectapp.credentialsDB

import androidx.room.*
import com.example.finalprojectapp.crypto.Cryptography
import com.example.finalprojectapp.data.model.Credentials
import com.example.finalprojectapp.data.model.DataSet
import com.example.finalprojectapp.data.model.Service
import com.example.finalprojectapp.data.model.relationship.DataSetCredentialsManyToMany
import com.example.finalprojectapp.data.model.relationship.ServiceToDataSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
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


    suspend fun publicGetCredentialsID(dataSet: Long): Credentials {
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

    suspend fun publicGetServiceByName(string: String): Service?{
        val service= privateGetServiceByName(string) ?: return null

        val list= mutableListOf<DataSet>()
        service.dataSets.forEach {
            list.add(getDataSetByID(it.dataSetId))
        }
        return service.service.copy(dataSets = list)
    }

    @Transaction
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

    @Transaction
    @Query("SELECT * FROM service_  Where :name like name")
    suspend fun privateGetServiceByName(name:String): ServiceToDataSet?


    @Query("SELECT r.dataSetId FROM service_ s, dataSetCredentialsManyToMany r,dataSet_ d,credentials_ c Where :serviceName like s.name AND d.serviceId = s.serviceId AND c.credentialsId==:credentialsID AND d.dataSetId = r.dataSetId And r.credentialsId = c.credentialsId")
    suspend fun privateGetUnionServiceNameAndCredentialsHash(serviceName:String,credentialsID: Long): Long?

    @Query("SELECT r.DataSetCredentialsManyToManyID FROM  dataSetCredentialsManyToMany r , credentials_ c   Where :dataSetID = r.dataSetId AND r.credentialsId = c.credentialsId And c.hint Like :hints")
    suspend fun privateGetUnionDataSetAndCredentialsHash(dataSetID:Long,hints: String): Long?

    suspend fun publicGetUnionServiceNameAndCredentialsHash(service: Service, oldCredentials: Credentials, newCredentials: Credentials): Int?{
        val credentialsInner=publicInsertCredentials(oldCredentials)
        val dataSet=privateGetUnionServiceNameAndCredentialsHash(service.name,credentialsInner)
        val json = Json(JsonConfiguration.Stable)
        val data = newCredentials.hint.let {
            Converters.Data(it)
        }
        val hint=json.stringify(Converters.Data.serializer(), data)
        val manyId= dataSet?.let {
            privateGetUnionDataSetAndCredentialsHash(it,hint)
        }
        val newCre=publicInsertCredentials(newCredentials)
        return dataSet?.let { manyId?.let { it1 -> DataSetCredentialsManyToMany(it,newCre, it1) } }?.let { privateUpdateNewCre(it) }
    }


    @Update
    fun privateUpdateNewCre(vararg newManyToMany: DataSetCredentialsManyToMany):Int






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
    suspend fun getDataSetByHash(hash:String): DataSet {

        val dataSet=privateGetDataSetByHash(hash)
        val allData=privateGetDataSetToCredentials(dataSet.dataSetId)
        val listCredentials= mutableListOf<Credentials>()
        allData.forEach {
            listCredentials.add(publicGetCredentialsID(it.credentialsId))
        }
        return dataSet.copy(credentials = listCredentials)
    }

    //function to get all data by id value for the dataSet
    suspend fun getDataSetByID(id:Long): DataSet {

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

    @Query("SELECT * FROM credentials_ Where salt not null ")
    suspend fun getAllEncryptedCredentials(): List<Credentials>

    @Update
    fun updateCredentials(it1: Credentials) :Int
}