package com.example.finalprojectapp.credentialsDB

import androidx.room.Transaction
import com.example.finalprojectapp.crypto.LocalCryptography
import com.example.finalprojectapp.data.model.Credentials
import com.example.finalprojectapp.data.model.DataSet
import com.example.finalprojectapp.data.model.relationship.DataSetCredentialsManyToMany
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DataSetRepository @Inject constructor(
    private val credentialRepository: CredentialRepository,
    private val dataSetDAO: DataSetDAO,
    private val localCryptography: LocalCryptography
) {


    fun getLocalCredentialByDataSetID(dataSetId: Long) =
        dataSetDAO.publicGetAllCredentialsByDataSetID(dataSetId)

    fun getDataSetById(serviceID: Long) = dataSetDAO.publicGetAllDataSetsByServiceId(serviceID)


    fun publicGetAllHashCredentials() =
        dataSetDAO.publicGetAllHashCredentials()

    fun publicDeleteCredential(credentialID: Long, dataSetId: Long) {
        dataSetDAO.deleteFromRelationshipCredential(credentialID, dataSetId)
        val result = dataSetDAO.privateGetRelationshipCredential(credentialID)
        if (result.isEmpty())
            credentialRepository.deleteCredential(Credentials().copy(credentialsId = credentialID))
    }


    suspend fun getDataSetByID(id: Long): DataSet {
        val dataSet = dataSetDAO.privateGetDataSetByDataSetID(id)
        val allData = dataSetDAO.privateGetDataSetToCredentials(dataSet.dataSetId)
        val listCredentials = mutableListOf<Credentials>()
        allData.forEach {
            credentialRepository.publicGetCredentialsID(it.credentialsId)?.let { it1 ->
                listCredentials.add(
                    it1
                )
            }
        }
        return dataSet.copy(credentials = listCredentials)
    }

    suspend fun getDataSetByID2(id: Long): DataSet {
        val dataSets = dataSetDAO.privateGetDataSetToCredentials2(id)
        val newCredentials = mutableListOf<Credentials>()
        dataSets.forEach {
            newCredentials.add(
                Credentials(
                    it.credentialHints,
                    it.credentialData,
                    it.credentialEncryptPasswordHash,
                    it.credentialEncryptType,
                    it.credentialHash,
                    it.credentialIV,
                    it.CredentialSalt,
                    null,
                    it.credentialID
                )
            )
        }
        return DataSet(
            newCredentials, dataSets[0].dataSetHash, dataSets[0].dataSetName
            , dataSets[0].dataSetId, dataSets[0].serviceId
        )
    }

    suspend fun privateDeleteDataSet(dataSet: DataSet) {
        val rel = dataSetDAO.findAllRelationshipToDataSet(dataSet.dataSetId)
        rel.forEach {
            dataSetDAO.deleteDataSetRelationship(it)
        }
        dataSet.credentials?.forEach {
            credentialRepository.deleteCredential(it)
        }
        dataSetDAO.deleteDataSet(dataSet)
    }

    @Transaction
    suspend fun deleteDataSetById(dataSetId: Long) {
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
        return dataSetDAO.privateGetUnionDataSetAndCredentialsHash(it, hint)
    }

    fun privateUpdateNewCre(it: DataSetCredentialsManyToMany): Int {
        return dataSetDAO.privateUpdateNewCre(it)
    }

    suspend fun publicInsertDataSet(dataSet: DataSet): Pair<Long, List<Long>>? {
        return withContext(Dispatchers.IO) {
            val target = localCryptography.encrypt(dataSet.copy()) ?: return@withContext null
            target.let {
                var result = privateInsertDataSet(it)
                if (result == -1L) {
                    val temp = dataSetDAO.privateGetDataSetByHash(it.hashData)
                    result = temp?.dataSetId ?: return@withContext null
                }
                val creList = mutableListOf<Long>()
                dataSet.credentials?.forEach { cre ->
                    val insertResult = credentialRepository.publicInsertCredentials(cre)
                    privateInsertCredentials(
                        DataSetCredentialsManyToMany(
                            dataSetId = result,
                            credentialsId = insertResult
                        )
                    )
                    creList.add(insertResult)
                }
                return@withContext Pair(result, creList)
            }
        }
    }

    suspend fun getDataSetByHash(hash: String): DataSet {

        val dataSet = dataSetDAO.privateGetDataSetByHash(hash)
        val allData = dataSetDAO.privateGetDataSetToCredentials(dataSet.dataSetId)
        val listCredentials = mutableListOf<Credentials>()
        allData.forEach {
            credentialRepository.publicGetCredentialsID(it.credentialsId)?.let { it1 ->
                listCredentials.add(
                    it1
                )
            }
        }
        return dataSet.copy(credentials = listCredentials)
    }

    suspend fun privateGetAllCredentials(): List<Credentials> {
        return credentialRepository.privateGetAllCredentials()
    }

    suspend fun publicInsertArrayCredentials(listCredentials: List<Credentials>): List<Long> {
        return credentialRepository.publicInsertArrayCredentials(listCredentials)
    }

    fun getAllData(): List<DataSet> {
        return dataSetDAO.publicGetAllDataSet()
    }


}