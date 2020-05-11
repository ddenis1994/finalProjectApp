package com.example.finalprojectapp.data

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.example.finalprojectapp.credentialsDB.LocalApplicationDAO
import com.example.finalprojectapp.data.model.DashBoardData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LocalRepository private constructor(
    private val credentialsDao: LocalApplicationDAO
)  {
    private val user = FirebaseAuth.getInstance().currentUser!!
    private val db = FirebaseFirestore.getInstance()




    fun getDashBoardData(): LiveData<DashBoardData>   {
        return object : NetworkBoundResource<DashBoardData,DashBoardData>(){
            override fun saveCallResult(item: DashBoardData) {
                TODO("Not yet implemented")
            }

            override fun shouldFetch(data: DashBoardData?): Boolean {
                TODO("Not yet implemented")
            }

            override fun loadFromDb(): LiveData<DashBoardData> {
                TODO("Not yet implemented")
            }


        }.asLiveData()

    }


    abstract class NetworkBoundResource<ResultType, RequestType> {
        // Called to save the result of the API response into the database
        @WorkerThread
        protected abstract fun saveCallResult(item: RequestType)

        // Called with the data in the database to decide whether to fetch
        // potentially updated data from the network.
        @MainThread
        protected abstract fun shouldFetch(data: ResultType?): Boolean

        // Called to get the cached data from the database.
        @MainThread
        protected abstract fun loadFromDb(): LiveData<ResultType>

        // Called to create the API call.
        //@MainThread
        //protected abstract fun createCall(): LiveData<ApiResponse<RequestType>>

        // Called when the fetch fails. The child class may want to reset components
        // like rate limiter.
        protected open fun onFetchFailed() {}

        // Returns a LiveData object that represents the resource that's implemented
        // in the base class.
        fun asLiveData(): LiveData<ResultType> = TODO()
    }

    fun getAllData() =
        credentialsDao.publicGetAllServiceName()

    fun getNumOfServices() =
        credentialsDao.publicGetNumOfServices()

    fun getService(name:String) =
        credentialsDao.publicGetServiceByNameLive(name)

    suspend fun deleteDataSet(dataSetId:Long){
        deleteFromRemote(dataSetId)
        credentialsDao.deleteDataSetById(dataSetId)
    }

    fun publicGetAllHashCredentials()=
        credentialsDao.publicGetAllHashCredentials()

    private suspend fun deleteFromRemote(dataSetId: Long) {
        val serviceName=credentialsDao.getServiceByDataSetId(dataSetId)
        val dataSet=credentialsDao.getDataSetByID(dataSetId)
        if (serviceName != null) {
            dataSet.hashData?.let {
                db.collection("users").document(user.uid)
                    .collection("services").document(serviceName)
                    .collection("dataSets").document(it)
                    .delete()
                    .addOnSuccessListener {
                        GlobalScope.launch {
                            withContext(Dispatchers.IO){
                                credentialsDao.deleteDataSetById(dataSetId)
                            }
                        }
                    }
            }
        }

    }


    fun getCredentialByDataSetID(dataSetId: Long)=credentialsDao.publicGetAllCredentialsByDataSetID(dataSetId)

    fun getDataSetById(serviceID: Long)=credentialsDao.publicGetAllDataSetsByServiceId(serviceID)

    suspend fun findServiceAndDataSet(dataSetId: Long) = credentialsDao.publicFindServiceAndDataSet(dataSetId)

    companion object {
        // For Singleton instantiation
        @Volatile private var instance: LocalRepository? = null
        fun getInstance(credentialsDao: LocalApplicationDAO) =
            instance ?: synchronized(this) {
                instance
                    ?: LocalRepository(
                        credentialsDao
                    )
                        .also { instance = it }
            }
    }
}