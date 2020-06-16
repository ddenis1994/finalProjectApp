package com.example.finalprojectapp.credentialsDB

import androidx.lifecycle.LiveData
import androidx.room.Transaction
import com.example.finalprojectapp.crypto.HashBuilder
import com.example.finalprojectapp.crypto.LocalCryptography
import com.example.finalprojectapp.data.model.DataSet
import com.example.finalprojectapp.data.model.Service
import com.example.finalprojectapp.data.model.adpters.LayoutCredentialView
import com.example.finalprojectapp.data.model.adpters.LayoutDataSetView
import com.example.finalprojectapp.ui.dashboard.DashboardViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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

    suspend fun publicInsertService(service: Service): Service {
        var localService: Service =
            privateGetServiceByName(service.name) ?: Service().copy(serviceId = -1L)
        var target = HashBuilder().makeHash(service)
        if (localService.hash == target?.hash) return localService
        if (localService.serviceId != -1L) {
            localService =
                localCryptography.decryption(localService) ?: Service().copy(serviceId = -1L)
            val newDataSetList = mutableListOf<DataSet>()
            service.dataSets?.let { newDataSetList.addAll(it) }
            localService.dataSets?.let { newDataSetList.addAll(it) }
            target = target?.copy(dataSets = newDataSetList)
            deleteFullServiceByID(localService)
        }
        target = localCryptography.encrypt(target)!!


        val result = target.let { serviceDAO.privateInsertService(it) }
        val list = mutableListOf<Pair<Long, List<Long>>>()
        target.dataSets?.forEach {
            // TODO: 16/06/2020 fix this with multi values
//            dataSetRepository.publicInsertDataSet(it.copy(serviceId = result))
//                .let { it1 ->
//                    if (it1 != null) {
//                        list.add(it1)
//                    }
//                }
        }

        return localCryptography.decryption(target) ?: Service()
    }


    fun getAllData() =
        serviceDAO.publicGetAllServiceName()

    fun getNumOfServices() =
        serviceDAO.publicGetNumOfServices()


    suspend fun findServiceAndDataSet(dataSetId: Long) =
        serviceDAO.publicFindServiceAndDataSet(/*dataSetId*/)


    suspend fun publicGetServiceByName(string: String): Service? {
        val service = privateGetServiceByName(string) ?: return null

        val list = mutableListOf<DataSet>()
        service.dataSets?.forEach {
            dataSetRepository.getDataSetByID(it.dataSetId)?.let { it1 -> list.add(it1) }
        }
        return service.copy(dataSets = list)
    }

    suspend fun publicGetAllServiceSuspand(): List<Service> {
        val service = serviceDAO.privateGetAllService()
        val servicesList = mutableListOf<Service>()
        service.forEach { ser ->
            val list = mutableListOf<DataSet>()
            ser.dataSets.forEach {
                dataSetRepository.getDataSetByID(it.dataSetId)?.let { it1 -> list.add(it1) }
            }
            servicesList.add(ser.service.copy(dataSets = list))

        }
        return servicesList
    }

    private suspend fun privateGetServiceByName(string: String): Service? {
        val service = serviceDAO.privateGetServiceByNameQuery(string) ?: return null
        val list = mutableListOf<DataSet>()
        service.dataSets.forEach {
            dataSetRepository.getDataSetByID(it.dataSetId)?.let { it1 -> list.add(it1) }
        }
        return service.service.copy(dataSets = list)
    }

//    suspend fun publicGetUnionServiceNameAndCredentialsHash(
//        service: Service,
//        oldCredentials: Credentials,
//        newCredentials: Credentials
//    ): Int? {
//        val credentialsInner = dataSetRepository.publicInsertCredentials(oldCredentials)
//        val dataSet =
//            serviceDAO.privateGetUnionServiceNameAndCredentialsHash(service.name, credentialsInner)
//        val json = Json(JsonConfiguration.Stable)
//        val data = newCredentials.hint.let {
//            Converters.Data(it)
//        }
//        val hint = json.stringify(Converters.Data.serializer(), data)
//        val manyId = dataSet?.let {
//            dataSetRepository.privateGetUnionDataSetAndCredentialsHash(it, hint)
//        }
//        val newCre = dataSetRepository.publicInsertCredentials(newCredentials)
//        return dataSet?.let { manyId?.let { it1 -> DataSetCredentialsManyToMany(it, newCre, it1) } }
//            ?.let { dataSetRepository.privateUpdateNewCre(it) }
//    }

    suspend fun deleteFullServiceByID(service: Service) {
        service.dataSets?.forEach {
            dataSetRepository.privateDeleteDataSet(it)
        }
        serviceDAO.privateDeleteService(service)
    }


    fun getDataSetById(dataSetId: Long): LiveData<List<LayoutDataSetView>> {
        return dataSetRepository.getDataSetByServiceId(dataSetId)
    }


    suspend fun deleteDataSetById(dataSetId: Long) {
        dataSetRepository.deleteDataSetById(dataSetId)
    }

    fun getServiceByDataSetId(dataSetId: Long): String? {
        return serviceDAO.getServiceByDataSetId(dataSetId)
    }

    suspend fun getDataSetByID(dataSetId: Long): DataSet? {
        return dataSetRepository.getDataSetByID(dataSetId)
    }

    suspend fun deleteLocalCredential(
        serviceName: String,
        credentialID: Long,
        dataSetId: Long
    ): Service {
        return withContext(Dispatchers.IO) {
            dataSetRepository.publicDeleteCredential(credentialID, dataSetId)
            return@withContext serviceDAO.privateGetServiceByName(serviceName) ?: Service()
        }
    }

    fun publicGetServiceNameByDataSetID(dataSetId: Long): String {
        return serviceDAO.getServiceByDataSetId(dataSetId) ?: ""
    }

    suspend fun deleteService(service: Service) {
        serviceDAO.privateDeleteService(service)
    }


}
