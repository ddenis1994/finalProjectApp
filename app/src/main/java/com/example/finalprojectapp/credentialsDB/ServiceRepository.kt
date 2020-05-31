package com.example.finalprojectapp.credentialsDB

import android.service.autofill.SaveCallback
import androidx.lifecycle.LiveData
import androidx.room.Transaction
import com.example.finalprojectapp.data.model.Credentials
import com.example.finalprojectapp.data.model.Service
import com.example.finalprojectapp.data.model.adpters.LayoutCredentialView
import com.example.finalprojectapp.data.model.adpters.LayoutDataSetView
import com.example.finalprojectapp.ui.dashboard.DashboardViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
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
            serviceRepositoryRemote.deleteRemoteCredential(result.await(),dataSet.await())
        }
    }


    //deleteDataSet
    suspend fun deleteDataSet(dataSetId: Long) {
        val serviceName = serviceRepositoryLocal.publicGetServiceNameByDataSetID(dataSetId)
        val service=serviceRepositoryLocal.publicGetServiceByName(serviceName)
        serviceRepositoryLocal.getDataSetByID(dataSetId)
        if (service != null) {
            serviceRepositoryRemote.deleteRemoteDataSet(service,dataSetId)
        }
        serviceRepositoryLocal.deleteDataSetById(dataSetId)
    }


    suspend fun publicGetServiceByName(string: String): Service? =
        serviceRepositoryLocal.publicGetServiceByName(string)


    suspend fun publicInsertLocalService(service: Service): Service {
        return serviceRepositoryLocal.publicInsertService(service)!!
    }


    suspend fun publicGetUnionServiceNameAndCredentialsHash(
        service: Service,
        oldCredentials: Credentials,
        newCredentials: Credentials
    ): Int? = serviceRepositoryLocal.publicGetUnionServiceNameAndCredentialsHash(
        service,
        oldCredentials,
        newCredentials
    )

    suspend fun publicGetAllServiceSuspend(): List<Service> =
        serviceRepositoryLocal.publicGetAllServiceSuspand()


    fun getDataSetById(dataSetId: Long): LiveData<List<LayoutDataSetView>> =
        serviceRepositoryLocal.getDataSetById(dataSetId)

    fun publicGetAllHashCredentials(): LiveData<List<DashboardViewModel.HashAndId>> =
        serviceRepositoryLocal.publicGetAllHashCredentials()

    fun getCredentialByDataSetID(dataSetId: Long): LiveData<List<LayoutCredentialView>> =
        serviceRepositoryLocal.getCredentialByDataSetID(dataSetId)

    suspend fun publicInsertCredentials(credential: Credentials) =
        serviceRepositoryLocal.publicInsertCredentials(credential)

    suspend fun privateGetAllCredentials(): List<Credentials> =
        serviceRepositoryLocal.privateGetAllCredentials()

    suspend fun publicInsertArrayCredentials(listCredentials: List<Credentials>): List<Long> =
        serviceRepositoryLocal.publicInsertArrayCredentials(listCredentials)

    fun updateRemotePassword(hash: String?) :Boolean {
        TODO("Not yet implemented")
    }


}

