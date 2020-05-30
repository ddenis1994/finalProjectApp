package com.example.finalprojectapp.credentialsDB

import android.content.Context
import android.service.autofill.SaveCallback
import androidx.lifecycle.LiveData
import androidx.room.Transaction
import com.example.finalprojectapp.data.model.Credentials
import com.example.finalprojectapp.data.model.Service
import com.example.finalprojectapp.data.model.adpters.LayoutCredentialView
import com.example.finalprojectapp.data.model.adpters.LayoutDataSetView
import com.example.finalprojectapp.ui.dashboard.DashboardViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class ServiceRepository @Inject constructor(
    context: Context,
    private val serviceRepositoryLocal: ServiceRepositoryLocal,
    notificationRepository: NotificationRepository
) {

    private val serviceRepositoryRemote: ServiceRepositoryRemote = ServiceRepositoryRemote(context,notificationRepository)
    private val scope = CoroutineScope(Job() + Dispatchers.Default)


    @Transaction
    suspend fun nukeALl() = serviceRepositoryLocal.nukeALl()

    fun addService(
        service: Service,
        callback: SaveCallback
    ) {
        scope.launch {
            serviceRepositoryRemote.addDataToRemoteWithSaveCallBack(service, callback)
        }
        scope.launch {
            serviceRepositoryLocal.publicInsertService(service)
        }

    }


    fun getAllData() = serviceRepositoryLocal.getAllData()

    fun getNumOfServices() =
        serviceRepositoryLocal.getNumOfServices()


    suspend fun findServiceAndDataSet(dataSetId: Long) =
        serviceRepositoryLocal.findServiceAndDataSet(dataSetId)


    fun deleteCredential(credentialID: Long?, dataSetId: Long) {
        if (credentialID != null) {
            scope.launch {
//                deleteLocalCredential(credentialID,dataSetId)
                //deleteRemoteCredential(dataSetId)
            }

        }
    }


    //deleteDataSet
    suspend fun deleteDataSet(dataSetId: Long) {
        val serviceName = serviceRepositoryLocal.getServiceByDataSetId(dataSetId)
        val dataSet = serviceRepositoryLocal.getDataSetByID(dataSetId)
        if (serviceName != null) {
            serviceRepositoryRemote.deleteFromRemote(serviceName, dataSet)
        }
        serviceRepositoryLocal.deleteDataSetById(dataSetId)
    }


    suspend fun publicGetServiceByName(string: String): Service? =
        serviceRepositoryLocal.publicGetServiceByName(string)


    suspend fun publicInsertLocalService(service: Service): Pair<Long, List<Pair<Long, List<Long>>>> {
        return serviceRepositoryLocal.publicInsertService(service)
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



}