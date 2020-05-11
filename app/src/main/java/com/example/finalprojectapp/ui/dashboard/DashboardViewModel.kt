package com.example.finalprojectapp.ui.dashboard

import android.util.Log
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.*
import androidx.room.ColumnInfo
import com.example.finalprojectapp.data.LocalRepository
import com.example.finalprojectapp.data.model.DashBoardData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DashboardViewModel internal constructor(
    private val mainRepository: LocalRepository
) : ViewModel() {
    

    private val _data =MutableLiveData<DashBoardData>()
    val data:LiveData<DashBoardData> = _data


    private val passwordCount=mainRepository.getNumOfServices()

    init {
        _data.postValue(DashBoardData())
    }



    private fun chalkForRepeatedPassword(): LiveData<Boolean> = liveData {
            withContext(Dispatchers.IO) {
                val repeatedList = mainRepository.publicGetAllHashCredentials().groupBy{it.id}.filter { it.value.size > 1 }
                _data.postValue(_data.value?.copy(connectionToRemote = false))
        }
    }





    fun click() {
        Log.i("test","Test")
    }

    fun showListServices(): Unit {
        //TODO make navigation to services fragment
        Log.i("test","Test")
    }

    data class HashAndId(
        @ColumnInfo(name = "credentialsId") val id: Long,
        @ColumnInfo(name = "hash") val hash : String,
        @ColumnInfo(name = "dataSet") val dataSetID : String
    )








}