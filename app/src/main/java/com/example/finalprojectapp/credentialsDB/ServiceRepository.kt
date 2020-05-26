package com.example.finalprojectapp.credentialsDB

import android.content.Context
import android.service.autofill.SaveCallback
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.room.Transaction
import com.example.finalprojectapp.crypto.Cryptography
import com.example.finalprojectapp.data.model.Credentials
import com.example.finalprojectapp.data.model.DataSet
import com.example.finalprojectapp.data.model.Service
import com.example.finalprojectapp.data.model.adpters.LayoutCredentialView
import com.example.finalprojectapp.data.model.adpters.LayoutDataSetView
import com.example.finalprojectapp.data.model.relationship.DataSetCredentialsManyToMany
import com.example.finalprojectapp.ui.dashboard.DashboardViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.security.MessageDigest
import java.util.*

class ServiceRepository private constructor(
    private val context: Context
) {
    private val dataSetRepository=DataSetRepository.getInstance(context)
    private val serviceDAO=LocalDataBase.getDatabase(context).serviceDao()
    private val user = FirebaseAuth.getInstance().currentUser!!
    private val db = FirebaseFirestore.getInstance()

    @Transaction
    suspend fun nukeALl() {
        dataSetRepository.deleteAllCredentials()
        dataSetRepository.deleteAllDataSets()
        dataSetRepository.deleteAllR()
        serviceDAO.deleteAllService()
    }

    suspend fun addService(
        service: Service,
        callback: SaveCallback
    ){
        withContext(Dispatchers.IO) {
            publicInsertService(service)
            addDataToRemoteWithSaveCallBack(service, callback)
        }
    }

    private fun addDataToRemoteWithSaveCallBack(
        service: Service,
        callback: SaveCallback
    ) {
        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser!!
        db.collection("users").document(user.uid)
            .collection("services").document(service.name)
            .set(service.copy(dataSets = null))
            .addOnSuccessListener {
                val cry= Cryptography(context)
                service.dataSets?.forEach {dataSet->
                    var rawData = dataSet.hashData
                    if (rawData.isNullOrEmpty()) {
                        rawData = String()
                        dataSet.credentials.let {
                            it?.forEach { cre ->
                                rawData += cre.data
                                rawData += cre.hint
                            }
                        }
                        val message: ByteArray = rawData.toByteArray()
                        val md = MessageDigest.getInstance("SHA-256")
                        rawData= Base64.getEncoder().encodeToString(md.digest(message))
                    }
                    val toUpload=cry.remoteEncryption(dataSet.copy(hashData = rawData))!!
                    db.collection("users").document(user.uid)
                        .collection("services").document(service.name)
                        .collection("dataSets").document(toUpload.hashData!!)
                        .set(toUpload)
                        .addOnSuccessListener {
                            callback.onSuccess()
                        }
                }
            }
    }





    fun getAllData() =
        serviceDAO.publicGetAllServiceName()

    fun getNumOfServices() =
        serviceDAO.publicGetNumOfServices()


    suspend fun findServiceAndDataSet(dataSetId: Long) = serviceDAO.publicFindServiceAndDataSet(dataSetId)


    fun deleteCredential(credentialID: Long?, dataSetId: Long) {
        if (credentialID != null) {
            GlobalScope.launch {
//                deleteLocalCredential(credentialID,dataSetId)
                deleteRemoteCredential(dataSetId)
            }

        }
    }

    private suspend fun deleteLocalCredential(credentialID: Long, dataSetId: Long){
        dataSetRepository.publicDeleteCredential(credentialID,dataSetId)
    }
    private suspend fun deleteRemoteCredential(dataSetId: Long){
        val serviceName=serviceDAO.getServiceByDataSetId(dataSetId)
        val dataSet=dataSetRepository.getDataSetByID(dataSetId)
//        val updates = hashMapOf<String, Any>(
//            "credentials" to dataSet.credentials!!
//        )
//        if (serviceName != null) {
//            dataSet.hashData?.let {
//                db.collection("users").document(user.uid)
//                    .collection("services").document(serviceName)
//                    .collection("dataSets").document(it)
//                    .update(updates)
//
//            }
//        }

    }

    suspend fun deleteDataSet(dataSetId:Long){
        deleteFromRemote(dataSetId)
        dataSetRepository.deleteDataSetById(dataSetId)
    }

    private suspend fun deleteFromRemote(dataSetId: Long) {
        val serviceName=serviceDAO.getServiceByDataSetId(dataSetId)
        val dataSet=dataSetRepository.getDataSetByID(dataSetId)
        if (serviceName != null) {
            dataSet.hashData?.let {
                db.collection("users").document(user.uid)
                    .collection("services").document(serviceName)
                    .collection("dataSets").document(it)
                    .delete()
                    .addOnSuccessListener {
                        GlobalScope.launch {
                            withContext(Dispatchers.IO){
                                dataSetRepository.deleteDataSetById(dataSetId)
                            }
                        }
                    }
            }
        }

    }


    fun publicInsertServiceSespend(toObject: Service):LiveData<Pair<Long,List<Pair<Long,List<Long>>>>> {
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



    suspend fun publicInsertService(service: Service):Pair<Long,List<Pair<Long,List<Long>>>>{
        var result=serviceDAO.privateInsertService(service)
        if (result==-1L){
            result= privateGetServiceByName(service.name)!!.serviceId
        }
        val list= mutableListOf<Pair<Long,List<Long>>>()
        service.dataSets?.forEach {
            publicInsertDataSet(it.copy(serviceId = result),service.name)?.let { it1 -> list.add(it1) }
        }
        return Pair(result,list)
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