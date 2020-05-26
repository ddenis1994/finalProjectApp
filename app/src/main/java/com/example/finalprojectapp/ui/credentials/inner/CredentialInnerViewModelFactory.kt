package com.example.finalprojectapp.ui.credentials.inner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.finalprojectapp.credentialsDB.ServiceRepository

class CredentialInnerViewModelFactory(private val repository: ServiceRepository): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CredentialInnerViewModel(repository) as T
    }
}
