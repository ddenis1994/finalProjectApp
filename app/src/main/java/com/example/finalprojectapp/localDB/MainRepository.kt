package com.example.finalprojectapp.localDB

import com.example.finalprojectapp.data.model.Service

class MainRepository private constructor(
    private val credentialsDao: CredentialsDAO
)  {

    fun getAllData() =
        credentialsDao.getAllServiceCredentialsPublic()




    fun getService(name:String) =
        credentialsDao.searchServiceCredentialsPublic(name)
    companion object {
        // For Singleton instantiation
        @Volatile private var instance: MainRepository? = null

        fun getInstance(credentialsDao: CredentialsDAO) =
            instance ?: synchronized(this) {
                instance ?: MainRepository(credentialsDao).also { instance = it }
            }
    }
}