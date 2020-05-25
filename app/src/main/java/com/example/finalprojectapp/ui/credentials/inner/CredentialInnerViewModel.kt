package com.example.finalprojectapp.ui.credentials.inner

import androidx.lifecycle.ViewModel
import com.example.finalprojectapp.data.LocalRepository

class CredentialInnerViewModel internal constructor(
    private val mainRepository: LocalRepository
) : ViewModel() {
    fun getCrede(dataSetId:Long)=mainRepository.getCredentialByDataSetID(dataSetId)
}
