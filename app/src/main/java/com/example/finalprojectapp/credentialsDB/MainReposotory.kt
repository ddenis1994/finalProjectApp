package com.example.finalprojectapp.credentialsDB


class MainRepository private constructor(
    private val credentialsDao: LocalApplicationDAO
)  {

    fun getAllData() =
        credentialsDao.publicGetAllServiceName()

    fun getService(name:String) =
        credentialsDao.publicGetServiceByNameLive(name)

    suspend fun deleteDataSet(dataSetId:Long)=credentialsDao.deleteDataSetById(dataSetId)

    fun getCredentialByDataSetID(dataSetId: Long)=credentialsDao.publicGetAllCredentialsByDataSetID(dataSetId)

    fun getDataSetById(serviceID: Long)=credentialsDao.publicGetAllDataSetsByServiceId(serviceID)

    companion object {
        // For Singleton instantiation
        @Volatile private var instance: MainRepository? = null
        fun getInstance(credentialsDao: LocalApplicationDAO) =
            instance ?: synchronized(this) {
                instance ?: MainRepository(credentialsDao).also { instance = it }
            }
    }
}