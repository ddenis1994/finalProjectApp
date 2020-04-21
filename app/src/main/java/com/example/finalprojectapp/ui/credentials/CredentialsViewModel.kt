package com.example.finalprojectapp.ui.credentials

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.finalprojectapp.credentialsDB.MainRepository
import com.example.finalprojectapp.credentialsDB.model.Service


class CredentialsViewModel internal constructor(
    mainRepository: MainRepository
    ) : ViewModel() {

    private val _allPasswords = mainRepository.getAllData()
    val allPasswords: LiveData<List<Service>> = _allPasswords

}