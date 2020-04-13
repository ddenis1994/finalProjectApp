package com.example.finalprojectapp.localDB

import androidx.lifecycle.LiveData
import com.example.finalprojectapp.data.model.Service

class MainRepository private constructor(
    private val credentialsDao: CredentialsDAO,
    private val serviceDAO: ServiceDAO
)  {

    fun getAllData() =
        credentialsDao.getAllServiceCredentialsPublic()

    fun insertListService(service: List<Service>) =
        credentialsDao.insertServiceCredentials(service)

    fun insertService(service: Service) =
        credentialsDao.insertSingleServiceCredentials(service)

    fun getService(name:String):LiveData<Service> =
        credentialsDao.searchServiceCredentialsPublic(name)






    companion object {

        // For Singleton instantiation
        @Volatile private var instance: MainRepository? = null

        fun getInstance(credentialsDao: CredentialsDAO,serviceDAO: ServiceDAO) =
            instance ?: synchronized(this) {
                instance ?: MainRepository(credentialsDao,serviceDAO).also { instance = it }
            }
    }
}