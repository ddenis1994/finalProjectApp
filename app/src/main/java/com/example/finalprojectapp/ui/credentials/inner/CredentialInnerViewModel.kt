package com.example.finalprojectapp.ui.credentials.inner

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.finalprojectapp.credentialsDB.ServiceRepository
import com.example.finalprojectapp.crypto.LocalCryptography
import com.example.finalprojectapp.data.model.Credentials
import com.example.finalprojectapp.data.model.adpters.LayoutCredentialView
import javax.inject.Inject

class CredentialInnerViewModel @Inject constructor(
    private val mainRepository: ServiceRepository
) : ViewModel() {

    private var _data = MutableLiveData<List<LayoutCredentialView>>()
    val data: LiveData<List<LayoutCredentialView>> = _data


    @Inject lateinit var cryptography:LocalCryptography

    fun firstTimeCredentials(dataSetId: Long) = mainRepository.getCredentialByDataSetID(dataSetId)


    fun updateData(dataPosition: Int) {
        val oldData = _data.value?.get(dataPosition)
        if (oldData != null) {
            val decryptedCredentials =
                decrepitCredentials(Credentials().copy(data = oldData.data!!, iv = oldData.iv))
            val newData = _data.value?.toMutableList()
            newData?.set(
                dataPosition, newData[dataPosition].copy(data = decryptedCredentials.data,iv = "")
            )
            _data.postValue(newData)
        }
    }

    private fun decrepitCredentials(cre: Credentials): Credentials {
        return cryptography.decryption(cre)!!
    }

    fun setData(data: List<LayoutCredentialView>) {
        _data.postValue(data)
    }

    fun deleteCredential(credentialID: Long?, dataSetId: Long) {
        mainRepository.deleteCredential(credentialID,dataSetId)
    }


}
