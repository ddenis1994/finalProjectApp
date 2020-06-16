package com.example.finalprojectapp.credentialsDB

import androidx.lifecycle.LiveData
import com.example.finalprojectapp.crypto.HashBuilder
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



    suspend fun publicInsertService(service: Service): Service? {
        val localService =
            getServiceByName(service.name)
        var target = localCryptography.encrypt(service)?: return null
        if (localService!=null){
            if (localService.hash == target.hash) return localService
            target.dataSets?.map { dataSetRepository.publicInsertDataSet(it.copy(serviceId = localService.serviceId)) }
            target=getServiceByName(service.name)!!
            serviceDAO.updateService(target)
            return localCryptography.decryption(target)
        }
        else{
            val result = serviceDAO.privateInsertService(target)
            target.dataSets?.map { dataSetRepository.publicInsertDataSet(it.copy(serviceId = result)) }
            target=getServiceByName(service.name)!!
            return localCryptography.decryption(target)
        }

    }


    fun getAllData() =
        serviceDAO.publicGetAllServiceName()

    fun getNumOfServices() =
        serviceDAO.publicGetNumOfServices()


    suspend fun findServiceAndDataSet(dataSetId: Long) =
        serviceDAO.findServiceAndDataSetsAndCredentials(dataSetId)


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

    // TODO: 16/06/2020 return private in production
    suspend fun getServiceByName(string: String): Service? {
        val service = serviceDAO.getServiceByName(string) ?: return null
        service.dataSets.forEach {
            val cre = dataSetRepository.getDataSetByID(it.dataSetId)?.credentials
            it.credentials = cre
        }
        return service.service.copy(dataSets = service.dataSets)
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
