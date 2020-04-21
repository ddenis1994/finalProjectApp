package com.example.finalprojectapp.credentialsDB


class MainRepository private constructor(
    private val credentialsDao: LocalApplicationDAO
)  {

    fun getAllData() =
        credentialsDao.publicGetAllService()

    fun getService(name:String) =
        credentialsDao.publicGetServiceByNameLive(name)

    fun deleteFullService(name: String)=credentialsDao.deleteFullService(name)

    companion object {
        // For Singleton instantiation
        @Volatile private var instance: MainRepository? = null
        fun getInstance(credentialsDao: LocalApplicationDAO) =
            instance ?: synchronized(this) {
                instance ?: MainRepository(credentialsDao).also { instance = it }
            }
    }
}