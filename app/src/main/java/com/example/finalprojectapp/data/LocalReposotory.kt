package com.example.finalprojectapp.data

import com.example.finalprojectapp.credentialsDB.LocalApplicationDAO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LocalRepository private constructor(
    private val credentialsDao: LocalApplicationDAO
)  {
    private val user = FirebaseAuth.getInstance().currentUser!!
    private val db = FirebaseFirestore.getInstance()


    fun getAllData() =
        credentialsDao.publicGetAllServiceName()

    fun getNumOfServices() =
        credentialsDao.publicGetNumOfServices()

    suspend fun deleteDataSet(dataSetId:Long){
        deleteFromRemote(dataSetId)
        credentialsDao.deleteDataSetById(dataSetId)
    }

    fun publicGetAllHashCredentials()=
        credentialsDao.publicGetAllHashCredentials()

    private suspend fun deleteFromRemote(dataSetId: Long) {
        val serviceName=credentialsDao.getServiceByDataSetId(dataSetId)
        val dataSet=credentialsDao.getDataSetByID(dataSetId)
        if (serviceName != null) {
            dataSet.hashData?.let {
                db.collection("users").document(user.uid)
                    .collection("services").document(serviceName)
                    .collection("dataSets").document(it)
                    .delete()
                    .addOnSuccessListener {
                        GlobalScope.launch {
                            withContext(Dispatchers.IO){
                                credentialsDao.deleteDataSetById(dataSetId)
                            }
                        }
                    }
            }
        }

    }


    fun getCredentialByDataSetID(dataSetId: Long)=credentialsDao.publicGetAllCredentialsByDataSetID(dataSetId)

    fun getDataSetById(serviceID: Long)=credentialsDao.publicGetAllDataSetsByServiceId(serviceID)

    suspend fun findServiceAndDataSet(dataSetId: Long) = credentialsDao.publicFindServiceAndDataSet(dataSetId)

    fun deleteCredential(credentialID: Long?) {
        if (credentialID != null) {
            GlobalScope.launch {
                deleteLocalCredential(credentialID)
            }

        }
    }

    private suspend fun deleteLocalCredential(credentialID: Long){
        credentialsDao.publicDeleteCredential(credentialID)
    }

    companion object {
        // For Singleton instantiation
        @Volatile private var instance: LocalRepository? = null
        fun getInstance(credentialsDao: LocalApplicationDAO) =
            instance ?: synchronized(this) {
                instance
                    ?: LocalRepository(
                        credentialsDao
                    )
                        .also { instance = it }
            }
    }
}