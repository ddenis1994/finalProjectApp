package com.example.finalprojectapp.ui.credentials

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.finalprojectapp.data.model.ServiceCredentialsServer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObjects

class CredentialsViewModel : ViewModel() {

    private val _allPasswords = MutableLiveData<List<ServiceCredentialsServer>>()
    val allPasswords: LiveData<List<ServiceCredentialsServer>> = _allPasswords

    fun getCredentialsData() {
        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser!!
        db.collection("users").document(user.uid)
            .collection("services").get()
            .addOnSuccessListener { documentSnapshot->
                val data=documentSnapshot.toObjects<ServiceCredentialsServer>()
                _allPasswords.postValue(data)
            }

    }
}