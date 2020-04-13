package com.example.finalprojectapp.data.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.finalprojectapp.data.model.Service
import com.example.finalprojectapp.localDB.MainRepository
import com.example.finalprojectapp.localDB.PasswordRoomDatabase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObjects

class CredentialsViewModel internal constructor(
    mainRepository: MainRepository
    ) : ViewModel() {

    private val _allPasswords = mainRepository.getAllData()
    val allPasswords: LiveData<List<Service>> = _allPasswords



    val data = mainRepository.getAllData()
}