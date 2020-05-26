package com.example.finalprojectapp.credentialsDB

import android.content.Context
import androidx.room.Transaction
import com.example.finalprojectapp.data.model.Credentials
import com.example.finalprojectapp.data.model.DataSet
import com.example.finalprojectapp.data.model.relationship.DataSetCredentialsManyToMany
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.util.*

class DataSetRepository private constructor(
    context: Context
) {



    private val credentialRepository=CredentialRepository.getInstance(context)

    private val dataSetDAO=LocalDataBase.getDatabase(context).dataSetDAO()

    fun getCredentialByDataSetID(dataSetId: Long)=dataSetDAO.publicGetAllCredentialsByDataSetID(dataSetId)

    fun getDataSetById(serviceID: Long)=dataSetDAO.publicGetAllDataSetsByServiceId(serviceID)


    fun publicGetAllHashCredentials()=
        dataSetDAO.publicGetAllHashCredentials()

    fun publicDeleteCredential(credentialID: Long, dataSetId: Long) {
        dataSetDAO.deleteFromRelationshipCredential(credentialID,dataSetId)
        val result=dataSetDAO.privateGetRelationshipCredential(credentialID)
        if (result.isEmpty())
            credentialRepository.deleteCredential(Credentials().copy(credentialsId = credentialID))
    }


    suspend fun getDataSetByID(id:Long): DataSet {
        val dataSet=dataSetDAO.privateGetDataSetByDataSetID(id)
        val allData=dataSetDAO.privateGetDataSetToCredentials(dataSet.dataSetId)
        val listCredentials= mutableListOf<Credentials>()
        allData.forEach {
            listCredentials.add(credentialRepository.publicGetCredentialsID(it.credentialsId))
        }
        return dataSet.copy(credentials = listCredentials)
    }

    fun privateDeleteDataSet(dataSet: DataSet){
        val rel=dataSetDAO.findAllRelationshipToDataSet(dataSet.dataSetId)
        rel.forEach {
            dataSetDAO.deleteDataSetRelationship(it)
        }
        dataSetDAO.deleteDataSet(dataSet)
    }

    @Transaction
    fun deleteDataSetById(dataSetId: Long) {
        dataSetDAO.deleteDataSet(DataSet().copy(dataSetId = dataSetId))
        dataSetDAO.deleteFromRelationship(dataSetId)
    }

    private suspend fun privateInsertDataSet(dataSet: DataSet): Long {
        return dataSetDAO.privateInsertDataSet(dataSet)
    }

    suspend fun publicInsertCredentials(credentials: Credentials): Long {
        return credentialRepository.publicInsertCredentials(credentials)
    }

    private suspend fun privateInsertCredentials(dataSetCredentialsManyToMany: DataSetCredentialsManyToMany): Long {
        return dataSetDAO.privateInsertCredentials(dataSetCredentialsManyToMany)
    }

    suspend fun deleteAllDataSets() {
        credentialRepository.deleteAllCredentials()
        dataSetDAO.deleteAllDataSets()
        dataSetDAO.deleteAllR()
    }



    suspend fun privateGetUnionDataSetAndCredentialsHash(it: Long, hint: String): Long? {
        return dataSetDAO.privateGetUnionDataSetAndCredentialsHash(it,hint)
    }

    fun privateUpdateNewCre(it: DataSetCredentialsManyToMany): Int {
        return dataSetDAO.privateUpdateNewCre(it)
    }

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
                hashData=hashData.replace("/","")
            }
            var result=privateInsertDataSet(dataSet.copy(hashData = hashData))
            if (result==-1L){
                result=dataSetDAO.privateGetDataSet(hashData).dataSetId
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

    suspend fun getDataSetByHash(hash:String): DataSet {

        val dataSet=dataSetDAO.privateGetDataSetByHash(hash)
        val allData=dataSetDAO.privateGetDataSetToCredentials(dataSet.dataSetId)
        val listCredentials= mutableListOf<Credentials>()
        allData.forEach {
            listCredentials.add(credentialRepository.publicGetCredentialsID(it.credentialsId))
        }
        return dataSet.copy(credentials = listCredentials)
    }

    suspend fun privateGetAllCredentials(): List<Credentials> {
        return credentialRepository.privateGetAllCredentials()
    }

    suspend fun publicInsertArrayCredentials(listCredentials: List<Credentials>): List<Long> {
        return credentialRepository.publicInsertArrayCredentials(listCredentials)
    }


    companion object {
        // For Singleton instantiation
        @Volatile
        private var instance: DataSetRepository? = null
        fun getInstance(context: Context) =
            instance ?: synchronized(this) {
                instance
                    ?: DataSetRepository(
                        context
                    )
                        .also { instance = it }
            }
    }
}