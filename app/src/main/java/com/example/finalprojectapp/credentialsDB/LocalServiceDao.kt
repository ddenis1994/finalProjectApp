package com.example.finalprojectapp.credentialsDB

import androidx.room.*
import com.example.finalprojectapp.credentialsDB.model.Credentials
import com.example.finalprojectapp.credentialsDB.model.DataSet
import com.example.finalprojectapp.credentialsDB.model.relationship.DataSetAndCredentials
import com.example.finalprojectapp.credentialsDB.model.relationship.DataSetCredentialsManyToMany
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

    @Query("SELECT * FROM credentials_ Where dataSetId = :dataSet ")
    suspend fun privateGetCredentialsByDataSet(dataSet: Long): List<Credentials>

    @Query("SELECT * FROM credentials_ Where innerHashValue = :dataSet ")
    suspend fun privateGetCredentialsByHashData(dataSet: String): Credentials

    suspend fun publicGetCredentialsByDataSet(dataSet: Long): List<Credentials>{
        val list= mutableListOf<Credentials>()
        val cryptography=Cryptography(null)
        privateGetCredentialsByDataSet(dataSet).forEach {
            list.add(cryptography.localDecryptCredentials(it)!!)
        }
        return list
    }


    suspend fun publicInsertCredentials(credentials: Credentials):Long{
        return withContext(Dispatchers.IO){
            val cryptography=Cryptography(null)
            var result= credentials.innerHashValue
            if (result==null){
                val message: ByteArray = (credentials.data+credentials.hint).toByteArray()
                val md = MessageDigest.getInstance("SHA-256")
                result= Base64.getEncoder().encodeToString(md.digest(message))
            }
            var resultInsert=privateInsertCredentials(cryptography.localEncryptCredentials(credentials.copy(innerHashValue = result!!))!!)
            if (resultInsert==-1L){
                resultInsert=privateGetCredentialsByHashData(result).credentialsId
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
                result=DataSet(privateGetDataSet(hashData!!)).dataSetId
            }
            val creList= mutableListOf<Long>()
            dataSet.credentials?.forEach {
                val insertResult=publicInsertCredentials(it.copy(dataSetId = result))
                privateInsertCredentials(DataSetCredentialsManyToMany(dataSetId=result,credentialsId = insertResult))
                creList.add(insertResult)
            }
            return@withContext Pair(result,creList)
        }
    }

    @Transaction
    @Query("SELECT * FROM dataSet_ ")
    suspend fun privateGetAllDataSet(): List<DataSetAndCredentials>

    @Transaction
    @Query("SELECT * FROM dataSetCredentialsManyToMany ")
    suspend fun privateGetAllDataSetCredentialsManyToMany(): List<DataSetCredentialsManyToMany>

    @Transaction
    @Query("SELECT * FROM dataSet_ Where :hashData = hashData")
    suspend fun privateGetDataSet(hashData:String): DataSetAndCredentials

    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun privateInsertCredentials2(credentials: DataSet):Long


}