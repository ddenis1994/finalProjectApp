package com.example.finalprojectapp.ui.credentials

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.finalprojectapp.credentialsDB.MainRepository
import com.example.finalprojectapp.data.ViewServiceData


class CredentialsViewModel internal constructor(
    val mainRepository: MainRepository
    ) : ViewModel() {

    private val _allPasswords = mainRepository.getAllData()
    val allPasswords: LiveData<List<ViewServiceData>> = _allPasswords

    fun getCrede(dataSetId:Long)=mainRepository.getCredentialByDataSetID(dataSetId)

}