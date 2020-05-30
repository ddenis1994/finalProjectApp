package com.example.finalprojectapp.ui.dashboard

import android.util.Log
import androidx.lifecycle.*
import androidx.room.ColumnInfo
import com.example.finalprojectapp.adapters.DashBoardRecyclerRepeatedPasswordAdapter
import com.example.finalprojectapp.credentialsDB.ServiceRepository
import com.example.finalprojectapp.data.model.DashBoardData
import kotlinx.coroutines.launch
import javax.inject.Inject

class DashboardViewModel @Inject constructor(
    private val mainRepository: ServiceRepository
) : ViewModel() {


    private val _data = MediatorLiveData<DashBoardData>()
    val data:LiveData<DashBoardData> = _data


    init {
        _data.postValue(DashBoardData())
        _data.addSource(mainRepository.getNumOfServices()) {_data.postValue(_data.value?.copy(serviceCount = it))
        }
    }

    fun addReactedPasswordListener(owner: LifecycleOwner) {
        _data.addSource(chalkForRepeatedPassword(owner)) { _data.postValue(it) }
    }



    private fun chalkForRepeatedPassword(owner: LifecycleOwner): LiveData<DashBoardData>  {
        val liveDataAdapter=MutableLiveData<DashBoardData>()
        viewModelScope.launch {
            //delay(100)
                mainRepository.publicGetAllHashCredentials()
                    .observe(owner, Observer { data ->
                        var repeatedList = data.groupBy { it.id }.filter { it.value.size > 1 }
                        val oldList = _data.value?.passwordRepeated
                        if (!oldList.isNullOrEmpty())
                            repeatedList = repeatedList.filter { !oldList.contains(it.key) }
                        if (repeatedList.isNotEmpty()) {
                            liveDataAdapter.postValue(
                                _data.value?.copy(
                                    securityRisks = _data.value!!.securityRisks + repeatedList.size,
                                    repeatedPassport = repeatedList.size,
                                    passwordRepeated = repeatedList,
                                    viewAdapter = DashBoardRecyclerRepeatedPasswordAdapter(
                                        repeatedList.toList(),
                                        this@DashboardViewModel
                                    )
                                )
                            )
                        }
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

    suspend fun findServiceAndDataSet(dataSetId: Long) = mainRepository.findServiceAndDataSet(dataSetId)

    data class HashAndId(
        @ColumnInfo(name = "credentialsId") val id: Long,
        @ColumnInfo(name = "hash") val hash : String,
        @ColumnInfo(name = "dataSet") val dataSetID : String
    )








}