package com.example.finalprojectapp.credentialsDB

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.room.Transaction
import com.example.finalprojectapp.data.model.Credentials
import com.example.finalprojectapp.data.model.DataSet
import com.example.finalprojectapp.data.model.Service
import com.example.finalprojectapp.data.model.adpters.LayoutCredentialView
import com.example.finalprojectapp.data.model.adpters.LayoutDataSetView
import com.example.finalprojectapp.data.model.relationship.DataSetCredentialsManyToMany
import com.example.finalprojectapp.ui.dashboard.DashboardViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

class ServiceRepositoryLocal(context: Context) {

    private val dataSetRepository=DataSetRepository.getInstance(context)
    private val serviceDAO=LocalDataBase.getDatabase(context).serviceDao()

    @Transaction
    suspend fun nukeALl() {
        dataSetRepository.deleteAllDataSets()
        serviceDAO.deleteAllService()
    }

    fun publicInsertService(service: Service):Pair<Long,List<Pair<Long,List<Long>>>> = runBlocking{
        var result=serviceDAO.privateInsertService(service)
        if (result==-1L){
            result= privateGetServiceByName(service.name)!!.serviceId
        }
        val list= mutableListOf<Pair<Long,List<Long>>>()
        service.dataSets?.forEach {
            dataSetRepository.publicInsertDataSet(it.copy(serviceId = result)).let { it1 -> list.add(it1) }
        }
        return@runBlocking Pair(result,list)
    }

    fun getAllData() =
        serviceDAO.publicGetAllServiceName()

    fun getNumOfServices() =
        serviceDAO.publicGetNumOfServices()



    suspend fun findServiceAndDataSet(dataSetId: Long) = serviceDAO.publicFindServiceAndDataSet(dataSetId)

    suspend fun deleteLocalCredential(credentialID: Long, dataSetId: Long){
        dataSetRepository.publicDeleteCredential(credentialID,dataSetId)
    }

    suspend fun deleteDataSet(dataSetId:Long){
        dataSetRepository.deleteDataSetById(dataSetId)
    }

    fun publicInsertServiceSuspend(toObject: Service): LiveData<Pair<Long, List<Pair<Long, List<Long>>>>> {
        return liveData {
            withContext(Dispatchers.IO) {
                emit(publicInsertService(toObject))
            }
        }
    }



    suspend fun publicGetServiceByName(string: String): Service?{
        val service= privateGetServiceByName(string) ?: return null

        val list= mutableListOf<DataSet>()
        service.dataSets?.forEach {
            list.add(dataSetRepository.getDataSetByID(it.dataSetId))
        }
        return service.copy(dataSets = list)
    }

    fun publicGetAllService(): LiveData<List<Service>> {
        return liveData {
            emit(publicGetAllServiceSuspand())
        }

    }

    suspend fun publicGetUnionServiceNameAndCredentialsHash(service: Service, oldCredentials: Credentials, newCredentials: Credentials): Int?{
        val credentialsInner=dataSetRepository.publicInsertCredentials(oldCredentials)
        val dataSet=serviceDAO.privateGetUnionServiceNameAndCredentialsHash(service.name,credentialsInner)
        val json = Json(JsonConfiguration.Stable)
        val data = newCredentials.hint.let {
            Converters.Data(it)
        }
        val hint=json.stringify(Converters.Data.serializer(), data)
        val manyId= dataSet?.let {
            dataSetRepository.privateGetUnionDataSetAndCredentialsHash(it,hint)
        }
        val newCre=dataSetRepository.publicInsertCredentials(newCredentials)
        return dataSet?.let { manyId?.let { it1 -> DataSetCredentialsManyToMany(it,newCre, it1) } }?.let { dataSetRepository.privateUpdateNewCre(it) }
    }

    suspend fun publicGetAllServiceSuspand():List<Service>{
        val service= serviceDAO.privateGetAllService()
        val servicesList= mutableListOf<Service>()
        service.forEach {ser->
            val list= mutableListOf<DataSet>()
            ser.dataSets.forEach {
                list.add(dataSetRepository.getDataSetByID(it.dataSetId))
            }
            servicesList.add(ser.service.copy(dataSets = list))

        }
        return servicesList
    }

    fun publicGetServiceByNameLive(string: String):LiveData<Service?>{
        return liveData {
            withContext(Dispatchers.IO) {
                emit(privateGetServiceByName(string))
            }
        }
    }

    suspend fun privateGetServiceByName(string: String): Service?{
        val service = serviceDAO.privateGetServiceByNameQuery(string) ?: return null
        val list = mutableListOf<DataSet>()
        service.dataSets.forEach {
            list.add(dataSetRepository.getDataSetByID(it.dataSetId))
        }
        return service.service.copy(dataSets = list)
    }

    fun deleteFullService(service: String):LiveData<Boolean>{
        return liveData {
            withContext(Dispatchers.IO) {
                val serviceLocal = privateGetServiceByName(service)
                if (serviceLocal==null) {
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


    suspend fun publicInsertDataSet(
        dataSet: DataSet,
        serviceName: String
    ):Pair<Long,List<Long>>?{
        return withContext(Dispatchers.IO){
            val hashData: String? = dataSet.hashData ?: return@withContext null
            val service=privateGetServiceByName(serviceName) ?: return@withContext null

            if(dataSetRepository.privateFindByHashDataAndServiceId(hashData!!,service.serviceId)!=null) {
                return@withContext null
            }

            var result=dataSetRepository.privateInsertDataSet(dataSet.copy(hashData = hashData,serviceId = service.serviceId))
            if (result==-1L){
                result=dataSetRepository.privateFindByHashData(hashData).dataSetId
            }
            val creList= mutableListOf<Long>()
            dataSet.credentials?.forEach {
                val insertResult=dataSetRepository.publicInsertCredentials(it)
                dataSetRepository.privateInsertCredentials(DataSetCredentialsManyToMany(dataSetId=result,credentialsId = insertResult))
                creList.add(insertResult)
            }
            return@withContext Pair(result,creList)
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

    companion object {
        // For Singleton instantiation
        @Volatile
        private var instance: ServiceRepositoryLocal? = null
        fun getInstance(context: Context) =
            instance ?: synchronized(this) {
                instance
                    ?: ServiceRepositoryLocal(
                        context
                    )
                        .also { instance = it }
            }
    }


}
