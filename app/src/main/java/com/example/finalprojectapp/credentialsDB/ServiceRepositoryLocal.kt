package com.example.finalprojectapp.credentialsDB

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.room.Transaction
import com.example.finalprojectapp.crypto.LocalCryptography
import com.example.finalprojectapp.data.model.Credentials
import com.example.finalprojectapp.data.model.DataSet
import com.example.finalprojectapp.data.model.Service
import com.example.finalprojectapp.data.model.adpters.LayoutCredentialView
import com.example.finalprojectapp.data.model.adpters.LayoutDataSetView
import com.example.finalprojectapp.data.model.relationship.DataSetCredentialsManyToMany
import com.example.finalprojectapp.ui.dashboard.DashboardViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import javax.inject.Inject

class ServiceRepositoryLocal @Inject constructor(
    private val serviceDAO: ServiceDAO,
    private val dataSetRepository: DataSetRepository,
    private val localCryptography: LocalCryptography
) {


    @Transaction
    suspend fun nukeALl() {
        dataSetRepository.deleteAllDataSets()
        serviceDAO.deleteAllService()
    }

    suspend fun publicInsertService(service: Service): Pair<Long?, List<Pair<Long, List<Long>>>?> {
        val localService: Service =
            privateGetServiceByName(service.name) ?: Service().copy(serviceId = -1L)
        var target=service
        if (localService.serviceId!=-1L) {
            val newDataSetList = mutableListOf<DataSet>()
            service.dataSets?.let { newDataSetList.addAll(it) }
            localService.dataSets?.let { newDataSetList.addAll(it) }
            target = target.copy(dataSets = newDataSetList)
            deleteFullService(localService.name)
        }
        target = localCryptography.encrypt(target)!!
        if (localService.hash == target.hash) return Pair(localService.serviceId, null)



        val result = target.let { serviceDAO.privateInsertService(it) }
        val list = mutableListOf<Pair<Long, List<Long>>>()
        service.dataSets?.forEach {
            dataSetRepository.publicInsertDataSet(it.copy(serviceId = result))
                .let { it1 ->
                    if (it1 != null) {
                        list.add(it1)
                    }
                }
        }
        return Pair(result, list)
    }

    fun getAllData() =
        serviceDAO.publicGetAllServiceName()

    fun getNumOfServices() =
        serviceDAO.publicGetNumOfServices()


    suspend fun findServiceAndDataSet(dataSetId: Long) =
        serviceDAO.publicFindServiceAndDataSet(dataSetId)

    fun deleteDataSet(dataSetId: Long) {
        dataSetRepository.deleteDataSetById(dataSetId)
    }


    suspend fun publicGetServiceByName(string: String): Service? {
        val service = privateGetServiceByName(string) ?: return null

        val list = mutableListOf<DataSet>()
        service.dataSets?.forEach {
            list.add(dataSetRepository.getDataSetByID(it.dataSetId))
        }
        return service.copy(dataSets = list)
    }

    suspend fun publicGetUnionServiceNameAndCredentialsHash(
        service: Service,
        oldCredentials: Credentials,
        newCredentials: Credentials
    ): Int? {
        val credentialsInner = dataSetRepository.publicInsertCredentials(oldCredentials)
        val dataSet =
            serviceDAO.privateGetUnionServiceNameAndCredentialsHash(service.name, credentialsInner)
        val json = Json(JsonConfiguration.Stable)
        val data = newCredentials.hint.let {
            Converters.Data(it)
        }
        val hint = json.stringify(Converters.Data.serializer(), data)
        val manyId = dataSet?.let {
            dataSetRepository.privateGetUnionDataSetAndCredentialsHash(it, hint)
        }
        val newCre = dataSetRepository.publicInsertCredentials(newCredentials)
        return dataSet?.let { manyId?.let { it1 -> DataSetCredentialsManyToMany(it, newCre, it1) } }
            ?.let { dataSetRepository.privateUpdateNewCre(it) }
    }

    suspend fun publicGetAllServiceSuspand(): List<Service> {
        val service = serviceDAO.privateGetAllService()
        val servicesList = mutableListOf<Service>()
        service.forEach { ser ->
            val list = mutableListOf<DataSet>()
            ser.dataSets.forEach {
                list.add(dataSetRepository.getDataSetByID(it.dataSetId))
            }
            servicesList.add(ser.service.copy(dataSets = list))

        }
        return servicesList
    }

    private suspend fun privateGetServiceByName(string: String): Service? {
        val service = serviceDAO.privateGetServiceByNameQuery(string) ?: return null
        val list = mutableListOf<DataSet>()
        service.dataSets.forEach {
            list.add(dataSetRepository.getDataSetByID(it.dataSetId))
        }
        return service.service.copy(dataSets = list)
    }

    fun deleteFullService(service: String): LiveData<Boolean> {
        return liveData {
            withContext(Dispatchers.IO) {
                val serviceLocal = privateGetServiceByName(service)
                if (serviceLocal == null) {
                    emit(false)
                    return@withContext
                }
                serviceLocal.dataSets?.forEach {
                    dataSetRepository.privateDeleteDataSet(it)
                }
                serviceDAO.privateDeleteService(serviceLocal)
                emit(true)
            }
        }
    }


    fun getDataSetById(dataSetId: Long): LiveData<List<LayoutDataSetView>> {
        return dataSetRepository.getDataSetById(dataSetId)
    }

    fun publicGetAllHashCredentials(): LiveData<List<DashboardViewModel.HashAndId>> {
        return dataSetRepository.publicGetAllHashCredentials()

    }

    fun getCredentialByDataSetID(dataSetId: Long): LiveData<List<LayoutCredentialView>> {
        return dataSetRepository.getCredentialByDataSetID(dataSetId)
    }

    suspend fun publicInsertCredentials(credential: Credentials) {
        dataSetRepository.publicInsertCredentials(credential)
    }

    suspend fun privateGetAllCredentials(): List<Credentials> {
        return dataSetRepository.privateGetAllCredentials()
    }

    suspend fun publicInsertArrayCredentials(listCredentials: List<Credentials>): List<Long> {
        return dataSetRepository.publicInsertArrayCredentials(listCredentials)

    }

    fun deleteDataSetById(dataSetId: Long) {
        dataSetRepository.deleteDataSetById(dataSetId)
    }

    fun getServiceByDataSetId(dataSetId: Long): String? {
        return serviceDAO.getServiceByDataSetId(dataSetId)
    }

    suspend fun getDataSetByID(dataSetId: Long): DataSet {
        return dataSetRepository.getDataSetByID(dataSetId)
    }


}
