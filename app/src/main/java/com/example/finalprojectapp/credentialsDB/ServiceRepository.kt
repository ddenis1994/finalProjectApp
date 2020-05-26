package com.example.finalprojectapp.credentialsDB

import android.content.Context
import android.service.autofill.SaveCallback
import androidx.lifecycle.LiveData
import androidx.room.Transaction
import com.example.finalprojectapp.data.model.Credentials
import com.example.finalprojectapp.data.model.DataSet
import com.example.finalprojectapp.data.model.Service
import com.example.finalprojectapp.data.model.adpters.LayoutCredentialView
import com.example.finalprojectapp.data.model.adpters.LayoutDataSetView
import com.example.finalprojectapp.ui.dashboard.DashboardViewModel
import kotlinx.coroutines.*

class ServiceRepository private constructor(
    context: Context
) {

    private val serviceRepositoryLocal: ServiceRepositoryLocal = ServiceRepositoryLocal(context)
    private val serviceRepositoryRemote: ServiceRepositoryRemote = ServiceRepositoryRemote(context)
    private val scope = CoroutineScope(Job() + Dispatchers.Default)


    @Transaction
    suspend fun nukeALl() = serviceRepositoryLocal.nukeALl()

    fun addService(
        service: Service,
        callback: SaveCallback
    ) {
        scope.launch {
            val test=serviceRepositoryLocal.publicGetAllServiceSuspand()
            val test2=serviceRepositoryLocal
            serviceRepositoryLocal.publicInsertService(service)
            serviceRepositoryRemote.addDataToRemoteWithSaveCallBack(service, callback)
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

    private suspend fun deleteLocalCredential(credentialID: Long, dataSetId: Long) {
        serviceRepositoryLocal.deleteLocalCredential(credentialID, dataSetId)
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


    suspend fun publicInsertService(service: Service): Pair<Long, List<Pair<Long, List<Long>>>> {
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

    fun publicGetServiceByNameLive(string: String): LiveData<Service?> =
        serviceRepositoryLocal.publicGetServiceByNameLive(string)


    fun deleteFullService(service: String): LiveData<Boolean> =
        serviceRepositoryLocal.deleteFullService(service)

    suspend fun publicInsertDataSet(
        dataSet: DataSet,
        serviceName: String
    ): Pair<Long, List<Long>>? = serviceRepositoryLocal.publicInsertDataSet(dataSet, serviceName)

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


    companion object {
        // For Singleton instantiation
        @Volatile
        private var instance: ServiceRepository? = null
        fun getInstance(context: Context) =
            instance ?: synchronized(this) {
                instance
                    ?: ServiceRepository(
                        context
                    )
                        .also { instance = it }
            }
    }
}