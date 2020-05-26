package com.example.finalprojectapp.ui.credentials.inner

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.finalprojectapp.crypto.Cryptography
import com.example.finalprojectapp.data.LocalRepository
import com.example.finalprojectapp.data.model.Credentials
import com.example.finalprojectapp.data.model.adpters.LayoutCredentialView

class CredentialInnerViewModel internal constructor(
    private val mainRepository: LocalRepository
) : ViewModel() {

    private var _data=MutableLiveData<List<LayoutCredentialView>>()
    val data:LiveData<List<LayoutCredentialView>> = _data

    fun firstTimeCredentials(dataSetId: Long)=mainRepository.getCredentialByDataSetID(dataSetId)




    fun updateData(dataPosition: Int) {
        val oldData= _data.value?.get(dataPosition)
        if (oldData!=null){
          val decryptedCredentials  =decrepitCredentials(Credentials().copy(data = oldData.data!!,iv = oldData.iv))
            val newData= _data.value?.toMutableList()
            newData?.set(dataPosition, LayoutCredentialView(decryptedCredentials.data,"",
                newData[dataPosition].hintList
            )
            )
            _data.postValue(newData)
        }
    }

    private fun decrepitCredentials(cre: Credentials): Credentials {
        val cryptography = Cryptography(null)
        return cryptography.decryptLocalSingleCredentials(cre)!!
    }

    fun setData(data: List<LayoutCredentialView>) {
        _data.postValue(data)
    }


}
