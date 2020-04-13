package com.example.finalprojectapp.data.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.finalprojectapp.localDB.MainRepository

class CredentialsViewModelFactory (
    private val repository: MainRepository
    ) : ViewModelProvider.NewInstanceFactory() {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CredentialsViewModel(repository) as T
        }
}