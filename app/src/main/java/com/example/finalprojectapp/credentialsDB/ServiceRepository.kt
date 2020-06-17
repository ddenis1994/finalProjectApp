package com.example.finalprojectapp.credentialsDB

import android.service.autofill.SaveCallback
import androidx.lifecycle.LiveData
import com.example.finalprojectapp.crypto.HashBuilder
import com.example.finalprojectapp.data.model.Service
import com.example.finalprojectapp.data.model.adpters.LayoutDataSetView
import kotlinx.coroutines.*
import javax.inject.Inject

class ServiceRepository @Inject constructor(
    private val serviceRepositoryLocal: ServiceRepositoryLocal,
    private val serviceRepositoryRemote: ServiceRepositoryRemote,
    private val scope: CoroutineScope
) {


    suspend fun nukeALl() = serviceRepositoryLocal.nukeALl()

    suspend fun addService(
        service: Service,
        callback: SaveCallback
    ) {
            val localInsert=scope.async {
                serviceRepositoryLocal.publicInsertService(service,callback)
            }
            localInsert.await().let {
                if (it != null) {
                    yield()
                    serviceRepositoryRemote.addDataToRemoteWithSaveCallBack(it)
                }
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


    suspend fun deleteDataSet(dataSetId: Long) {
        val serviceName = serviceRepositoryLocal.publicGetServiceNameByDataSetID(dataSetId)
        val dataSet=serviceRepositoryLocal.getDataSetByID(dataSetId)
        withContext(Dispatchers.IO) {
            val job=this.async {
                serviceRepositoryLocal.deleteDataSetById(dataSetId)

            }
            job.await()
            val service1=serviceRepositoryLocal.getServiceByName(serviceName)
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
        serviceRepositoryLocal.getServiceByName(string)


    fun getDataSetById(dataSetId: Long): LiveData<List<LayoutDataSetView>> =
        serviceRepositoryLocal.getDataSetById(dataSetId)

    fun updateRemotePassword(hash: String?) :Boolean {
        TODO("Not yet implemented")
    }


}

