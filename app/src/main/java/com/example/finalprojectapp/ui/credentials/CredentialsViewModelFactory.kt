package com.example.finalprojectapp.ui.credentials

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.finalprojectapp.credentialsDB.ServiceRepository


class CredentialsViewModelFactory (
    private val repository: ServiceRepository
    ) : ViewModelProvider.NewInstanceFactory() {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CredentialsViewModel(repository) as T
        }
}