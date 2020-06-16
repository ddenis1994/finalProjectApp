package com.example.finalprojectapp.credentialsDB

import android.service.autofill.SaveCallback
import androidx.lifecycle.LiveData
import androidx.room.Transaction
import com.example.finalprojectapp.crypto.HashBuilder
import com.example.finalprojectapp.data.model.Credentials
import com.example.finalprojectapp.data.model.Service
import com.example.finalprojectapp.data.model.adpters.LayoutCredentialView
import com.example.finalprojectapp.data.model.adpters.LayoutDataSetView
import com.example.finalprojectapp.ui.dashboard.DashboardViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

class ServiceRepository @Inject constructor(
    private val serviceRepositoryLocal: ServiceRepositoryLocal,
    private val serviceRepositoryRemote: ServiceRepositoryRemote,
    private val scope: CoroutineScope
) {


    @Transaction
    suspend fun nukeALl() = serviceRepositoryLocal.nukeALl()

    fun addService(
        service: Service,
        callback: SaveCallback
    ) {
        scope.launch {
            val localInsert=scope.async {
                serviceRepositoryLocal.publicInsertService(service)
            }
            localInsert.await().let {
                serviceRepositoryRemote.addDataToRemoteWithSaveCallBack(it, callback) }


        }
    }

    suspend fun sync() {
        serviceRepositoryRemote.sync()
    }


    fun getAllData() = serviceRepositoryLocal.getAllData()

    fun getNumOfServices() =
        serviceRepositoryLocal.getNumOfServices()


    suspend fun findServiceAndDataSet(dataSetId: Long) =
        serviceRepositoryLocal.findServiceAndDataSet(dataSetId)


    fun deleteCredential(
        serviceName: String,
        dataSetId: Long,
        credentialID: Long
    ) {
        scope.launch {
            val result=this.async { serviceRepositoryLocal.deleteLocalCredential(serviceName,credentialID,dataSetId) }
            val dataSet=this.async { serviceRepositoryLocal.getDataSetByID(dataSetId) }
            dataSet.await()?.let {
                serviceRepositoryRemote.deleteRemoteCredential(result.await(),
                    it
                )
            }
        }
    }


    suspend fun deleteDataSet(dataSetId: Long) {
        val serviceName = serviceRepositoryLocal.publicGetServiceNameByDataSetID(dataSetId)
        val dataSet=serviceRepositoryLocal.getDataSetByID(dataSetId)
        withContext(Dispatchers.IO) {
            val job=this.async {
                serviceRepositoryLocal.deleteDataSetById(dataSetId)

            }
            job.await()
            val service1=serviceRepositoryLocal.publicGetServiceByName(serviceName)
            val newHash=HashBuilder().makeHash(service1)
            if ( newHash?.dataSets!=null && ( newHash.dataSets!!.isNullOrEmpty())){
                serviceRepositoryLocal.deleteFullServiceByID( newHash)
            }
            if ( newHash != null) {
                dataSet?.hashData?.let { serviceRepositoryRemote.deleteRemoteDataSet(newHash, it) }
            }
        }
    }




    suspend fun publicGetServiceByName(string: String): Service? =
        serviceRepositoryLocal.publicGetServiceByName(string)


    suspend fun publicInsertLocalService(service: Service): Service {
        return serviceRepositoryLocal.publicInsertService(service)
    }


    suspend fun publicGetAllServiceSuspend(): List<Service> =
        serviceRepositoryLocal.publicGetAllServiceSuspand()


    fun getDataSetById(dataSetId: Long): LiveData<List<LayoutDataSetView>> =
        serviceRepositoryLocal.getDataSetById(dataSetId)

    fun updateRemotePassword(hash: String?) :Boolean {
        TODO("Not yet implemented")
    }


}

