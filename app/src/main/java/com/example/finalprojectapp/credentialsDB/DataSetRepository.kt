package com.example.finalprojectapp.credentialsDB

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

    fun getDataSetByServiceId(serviceID: Long) =
        dataSetDAO.publicGetAllDataSetsByServiceId(serviceID)


    fun publicGetAllHashCredentials() =
        dataSetDAO.publicGetAllHashCredentials()


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


    suspend fun publicInsertCredentials(credentials: Credentials): Long =
        // credentialRepository.publicInsertCredentials(credentials)
        -1L

    suspend fun deleteAllDataSets() {
        credentialRepository.deleteAllCredentials()
        dataSetDAO.deleteAllDataSets()
    }


    suspend fun privateGetUnionDataSetAndCredentialsHash(it: Long, hint: String): Long? =
        dataSetDAO.privateGetUnionDataSetAndCredentialsHash(it, hint)


    fun privateUpdateNewCre(it: DataSetCredentialsManyToMany): Int =
        dataSetDAO.privateUpdateNewCre(it)


    suspend fun publicInsertDataSet(vararg dataSets: DataSet): List<Pair<Long, List<Long>?>>? =
        withContext(Dispatchers.IO) {
            return@withContext dataSets.map { privateInsertDataSet(it) }
        }


    private suspend fun privateInsertDataSet(dataSet: DataSet): Pair<Long, List<Long>?> {
        val target =
            localCryptography.encrypt(dataSet.copy()) ?: return Pair(-1L, null)
        target.let { encryptedDataSet ->
            val result = dataSetDAO.privateInsertDataSet(encryptedDataSet)[0]
            if (result == -1L) {
                val oldResult = dataSetDAO.getDataSetByHash(encryptedDataSet.hashData)
                return if (oldResult?.credentials!!.isNotEmpty())
                    Pair(
                        oldResult.dataSet.dataSetId,
                        oldResult.credentials.map { cre -> cre.credentialsId })
                else
                    Pair(oldResult.dataSet.dataSetId, null)
            }
            val newCredentials = encryptedDataSet.credentials?.map { credential ->
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


}