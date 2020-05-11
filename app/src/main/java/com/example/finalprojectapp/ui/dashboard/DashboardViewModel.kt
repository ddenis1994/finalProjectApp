package com.example.finalprojectapp.ui.dashboard

import android.util.Log
import androidx.lifecycle.*
import androidx.room.ColumnInfo
import com.example.finalprojectapp.data.LocalRepository
import com.example.finalprojectapp.data.model.DashBoardData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DashboardViewModel internal constructor(
    private val mainRepository: LocalRepository
) : ViewModel() {


    private val _data = MediatorLiveData<DashBoardData>()
    val data:LiveData<DashBoardData> = _data


    init {
        _data.postValue(DashBoardData())
        _data.addSource(mainRepository.getNumOfServices()) {
            _data.postValue(_data.value?.copy(serviceCount = it))
        }
    }

    fun addReactedPasswordListener(owner: LifecycleOwner) {
        _data.addSource(chalkForRepeatedPassword(owner)) {
            if(it)
                _data.postValue(_data.value?.copy(securityRisks = _data.value!!.securityRisks+1))
        }
    }



    private fun chalkForRepeatedPassword(owner: LifecycleOwner): LiveData<Boolean>  {
        val liveDataAdapter=MutableLiveData<Boolean>()
        viewModelScope.launch {
                mainRepository.publicGetAllHashCredentials()
                    .observe(owner, Observer { data ->
                        val repeatedList = data.groupBy { it.id }.filter { it.value.size > 1 }
                        if (repeatedList.isNullOrEmpty())
                            liveDataAdapter.postValue(false)
                        else
                            liveDataAdapter.postValue(true)
                    })
        }
        return liveDataAdapter

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