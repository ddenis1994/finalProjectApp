package com.example.finalprojectapp.ui.dashboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.room.ColumnInfo
import com.example.finalprojectapp.adapters.DashBoardRecyclerRepeatedPasswordAdapter
import com.example.finalprojectapp.credentialsDB.ServiceRepository
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class DashboardViewModel @Inject constructor(
    private val mainRepository: ServiceRepository
) : ViewModel() {

    private var _serviceCount = MutableLiveData(-1)
    val serviceCount: LiveData<Int> = _serviceCount

    private val _securityRisks = MutableLiveData(0)
    val securityRisks: LiveData<Int> = _securityRisks

    private val _connectionToRemote=MutableLiveData(
        FirebaseAuth.getInstance().currentUser != null
    )
    val connectionToRemote:LiveData<Boolean> = _connectionToRemote



    private var _repeatedPassport = 0


    private var _passwordRepeated: Map<String?, List<ServiceNameAndDataSet?>>? = null


    private val _viewAdapter = MutableLiveData<DashBoardRecyclerRepeatedPasswordAdapter?>()
    val viewAdapter:LiveData<DashBoardRecyclerRepeatedPasswordAdapter?> = _viewAdapter


    fun getNumOfService() = mainRepository.getNumOfServices()

    fun checkForRepeatedPassword()=mainRepository.checkForRepeatedPassword()


    fun click() {
        Log.i("test", "Test")
    }

    fun showListServices(): Unit {
        //TODO make navigation to services fragment
        Log.i("test", "Test")
    }

    fun updateServiceCount(it: Int) {
        _serviceCount.postValue(it)
    }

    fun updateRepeatedPassword(password: List<ServiceNameAndDataSet?>?) {
        var list = password?.groupBy { it?.hash }?.filter { it.value.size > 1 }
        val oldList = _passwordRepeated
        if (!oldList.isNullOrEmpty())
            list = list?.filter { !oldList.contains(it.key) }
        if (!list.isNullOrEmpty()) {
            _securityRisks.postValue(securityRisks.value?.plus(list.size))
            _repeatedPassport=list.size
            _passwordRepeated=list
            val temp=DashBoardRecyclerRepeatedPasswordAdapter(list.toList())
            _viewAdapter.postValue(temp)
        }
    }

    data class ServiceNameAndDataSet(
        @ColumnInfo(name = "serviceName") val serviceName: String,
        @ColumnInfo(name = "hash") val hash: String,
        @ColumnInfo(name = "dataSetName") val dataSetName: String
    )


}