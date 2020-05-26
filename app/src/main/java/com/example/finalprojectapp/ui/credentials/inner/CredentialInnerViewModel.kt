package com.example.finalprojectapp.ui.credentials.inner

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.finalprojectapp.data.LocalRepository
import com.example.finalprojectapp.data.model.adpters.LayoutCredentialView
import com.example.finalprojectapp.databinding.LayoutCredentialBinding

class CredentialInnerViewModel internal constructor(
    private val mainRepository: LocalRepository
) : ViewModel() {

    private var _data=MutableLiveData<List<LayoutCredentialView>>()
    private var data:LiveData<List<LayoutCredentialView>> = _data



    fun getCrede(dataSetId:Long): LiveData<List<LayoutCredentialView>> {
        data= mainRepository.getCredentialByDataSetID(dataSetId)
        return data
    }
}
