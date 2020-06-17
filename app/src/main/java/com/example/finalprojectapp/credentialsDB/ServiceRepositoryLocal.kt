package com.example.finalprojectapp.credentialsDB

import android.service.autofill.SaveCallback
import androidx.lifecycle.LiveData
import com.example.finalprojectapp.crypto.LocalCryptography
import com.example.finalprojectapp.data.model.DataSet
import com.example.finalprojectapp.data.model.Service
import com.example.finalprojectapp.data.model.adpters.LayoutDataSetView
import javax.inject.Inject

class ServiceRepositoryLocal @Inject constructor(
    private val serviceDAO: ServiceDAO,
    private val dataSetRepository: DataSetRepository,
    private val localCryptography: LocalCryptography
) {


    suspend fun nukeALl() {
        dataSetRepository.deleteAllDataSets()
        serviceDAO.deleteAllService()
    }


    suspend fun publicInsertService(
        service: Service,
        callback: SaveCallback?
    ): Service? {
        val localService =
            getServiceByName(service.name)
        val target = localCryptography.encrypt(service) ?: return null
        val temp = if (localService != null) {
            if (localService.hash == target.hash) return localService
            updateExistedService(target.copy(serviceId = localService.serviceId))
        } else {
            insertNewService(target)
        }
        callback?.onSuccess()
        return localCryptography.decryption(temp)
    }

    suspend fun updateExistedService(service: Service): Service? {
        var target: Service = service
        target.dataSets?.map { dataSetRepository.publicInsertDataSet(it.copy(serviceId = target.serviceId)) }
        target = getServiceByName(target.name)!!
        serviceDAO.updateService(target)
        return target
    }

    private suspend fun insertNewService(service: Service): Service? {
        val result = serviceDAO.privateInsertService(service)
        service.dataSets?.map { dataSetRepository.publicInsertDataSet(it.copy(serviceId = result)) }
        return getServiceByName(service.name)!!
    }


    fun getAllData() =
        serviceDAO.publicGetAllServiceName()

    fun getNumOfServices() =
        serviceDAO.publicGetNumOfServices()


    suspend fun findServiceAndDataSet(dataSetId: Long) =
        serviceDAO.findServiceAndDataSetsAndCredentials(dataSetId)

    fun checkForRepeatedPassword() = serviceDAO.checkForRepeatedPassword()


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

    suspend fun getServiceByName(string: String): Service? {
        val service = serviceDAO.getServiceByName(string) ?: return null
        service.dataSets.forEach {
            val cre = dataSetRepository.getDataSetByID(it.dataSetId)?.credentials
            it.credentials = cre
        }
        return localCryptography.decryption(service.service.copy(dataSets = service.dataSets))
    }

    suspend fun deleteFullServiceByID(service: Service) {
        service.dataSets?.forEach {
            dataSetRepository.privateDeleteDataSet(it)
        }
        serviceDAO.deleteService(service)
    }


    fun getDataSetById(dataSetId: Long): LiveData<List<LayoutDataSetView>> {
        return dataSetRepository.getDataSetByServiceId(dataSetId)
    }


    suspend fun deleteDataSetById(dataSetId: Long) {
        dataSetRepository.deleteDataSetById(dataSetId)
    }

    suspend fun getDataSetByID(dataSetId: Long): DataSet? {
        return dataSetRepository.getDataSetByID(dataSetId)
    }

    fun publicGetServiceNameByDataSetID(dataSetId: Long): String {
        return serviceDAO.getServiceByDataSetId(dataSetId)?.service?.name ?: ""
    }


}
