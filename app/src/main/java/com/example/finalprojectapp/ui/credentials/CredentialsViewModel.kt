package com.example.finalprojectapp.ui.credentials

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.finalprojectapp.credentialsDB.ServiceRepository
import com.example.finalprojectapp.data.model.adpters.LayoutServiceView
import javax.inject.Inject


class CredentialsViewModel @Inject constructor(
    private val mainRepository: ServiceRepository
    ) : ViewModel() {

    private val _allPasswords = mainRepository.getAllData()
    val allPasswords: LiveData<List<LayoutServiceView>> = _allPasswords

    fun getDataSet(dataSetId:Long)=mainRepository.getDataSetById(dataSetId)

    suspend fun deleteDataSet(dataSetId:Long)=mainRepository.deleteDataSet(dataSetId)

    suspend fun sync() {
        mainRepository.sync()
    }

}