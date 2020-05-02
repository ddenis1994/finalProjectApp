package com.example.finalprojectapp.ui.credentials

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.finalprojectapp.credentialsDB.MainRepository
import com.example.finalprojectapp.data.model.adpters.LayoutServiceView


class CredentialsViewModel internal constructor(
    private val mainRepository: MainRepository
    ) : ViewModel() {

    private val _allPasswords = mainRepository.getAllData()
    val allPasswords: LiveData<List<LayoutServiceView>> = _allPasswords

    fun getCrede(dataSetId:Long)=mainRepository.getCredentialByDataSetID(dataSetId)

    fun getDataSet(dataSetId:Long)=mainRepository.getDataSetById(dataSetId)

    suspend fun deleteDataSet(dataSetId:Long)=mainRepository.deleteDataSet(dataSetId)

}