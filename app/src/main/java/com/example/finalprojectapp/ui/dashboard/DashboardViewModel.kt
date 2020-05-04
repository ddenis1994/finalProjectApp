package com.example.finalprojectapp.ui.dashboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.ColumnInfo
import com.example.finalprojectapp.data.LocalRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DashboardViewModel internal constructor(
    private val mainRepository: LocalRepository
) : ViewModel() {
    

    var serviceCount: Int=-1
    val securityRisks: Int=-1
    val connectionToRemote:Boolean= FirebaseAuth.getInstance().currentUser != null
    val passwordStrange:Int=4

    private val samePassword=MutableLiveData<Boolean>()


    fun getDataFromLocalService(): LiveData<Int> {
        chalkForRepeatedPassword()
      return mainRepository.getNumOfServices()
    }

    data class HashAndId(
        @ColumnInfo(name = "credentialsId") val id: Long,
        @ColumnInfo(name = "innerHashValue") val HashData : String
    )

    private fun chalkForRepeatedPassword(): Unit {
        this.viewModelScope.launch {
            withContext(Dispatchers.IO) {
                //TODO countinue serching for repated credentiansls
                val repatedList = mainRepository.publicGetAllHashCredentials().groupBy { it.HashData }.filter { it.value.size > 0 }
                Log.i("test","tes")
                print(repatedList.toString())

            }
        }
    }





    fun click() {
        Log.i("test","Test")
    }

    fun showListServices(): Unit {
        //TODO make navigation to services fragment
        Log.i("test","Test")
    }




}