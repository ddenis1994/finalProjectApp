package com.example.finalprojectapp.credentialsDB

import com.example.finalprojectapp.crypto.LocalCryptography
import com.example.finalprojectapp.data.model.Credentials
import com.example.finalprojectapp.data.model.DataSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import javax.inject.Inject

class DataSetRepository @Inject constructor(
    private val credentialRepository: CredentialRepository,
    private val dataSetDAO: DataSetDAO,
    private val localCryptography: LocalCryptography
) {


    fun getDataSetByServiceId(serviceID: Long) =
        dataSetDAO.publicGetAllDataSetsByServiceId(serviceID)


    suspend fun publicDeleteCredential(credentialID: Long, dataSetId: Long) {
        credentialRepository.deleteCredential(Credentials().copy(credentialsId = credentialID))
        val result = dataSetDAO.getDataSetWithCredentialsByDataSetID(dataSetId)
        if (result?.credentials!!.isEmpty())
            dataSetDAO.deleteDataSet(DataSet().copy(dataSetId = dataSetId))
    }


    suspend fun getDataSetByID(id: Long): DataSet? {
        val dataSet = dataSetDAO.getDataSetWithCredentialsByDataSetID(id)
        return dataSet?.dataSet?.copy(credentials = dataSet.credentials)
    }

    suspend fun privateDeleteDataSet(dataSet: DataSet) {
        dataSetDAO.deleteDataSet(dataSet)
        credentialRepository.deleteCredentialByDataSetID(dataSet.dataSetId)
    }

    suspend fun deleteDataSetById(dataSetId: Long) {
        privateDeleteDataSet(DataSet().copy(dataSetId = dataSetId))
    }


    suspend fun deleteAllDataSets() {
        credentialRepository.deleteAllCredentials()
        dataSetDAO.deleteAllDataSets()
    }


    suspend fun publicInsertDataSet(vararg dataSets: DataSet): List<Pair<Long, List<Long>?>>? {
        return dataSets.map { privateInsertDataSet(it) }.toList()
    }


    private suspend fun privateInsertDataSet(dataSet: DataSet): Pair<Long, List<Long>?> {
        val target =
            localCryptography.encrypt(dataSet) ?: return Pair(-1L, null)
        var result = dataSetDAO.privateInsertDataSet(target)[0]
        if (result == -1L) {
            val oldResult =
                target.serviceId?.let { dataSetDAO.getDataSetByNameAndServiceID(it,target.dataSetName) }
            oldResult?.dataSet?.let { privateDeleteDataSet(it) }
            result = dataSetDAO.privateInsertDataSet(target)[0]
            if (result== -1L) return Pair(result,null)
        }
        val newCredentials = target.credentials?.map { credential ->
            credential.copy(credentialDataSetId = result)
        }?.toTypedArray()
        val credentialsList = newCredentials?.let { credential ->
            credentialRepository.insertCredentials(
                *credential
            )
        }
        return Pair(result, credentialsList)
    }


}