package com.example.finalprojectapp.ui.credentials

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.finalprojectapp.data.model.Credentials
import com.example.finalprojectapp.data.model.Service
import com.example.finalprojectapp.data.model.ServicesAndPasswords

class CredentialsViewModel : ViewModel() {

    private val _allPasswords = MutableLiveData<List<ServicesAndPasswords>>().apply {
        var test= mutableListOf<ServicesAndPasswords>()
        var t= ServicesAndPasswords(
            Service(0,"test"),
            mutableListOf<Credentials>().apply {
                add(Credentials(0,0,"testCre","test","test","test"))
            }
        )

        test.add(t)
        test.add(t)
        value=test
    }
    val allPasswords: LiveData<List<ServicesAndPasswords>> = _allPasswords
}