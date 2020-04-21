package com.example.finalprojectapp.autoFillService.adapters

import android.view.View
import com.example.finalprojectapp.autoFillService.AutoFillNodeData
import com.example.finalprojectapp.credentialsDB.LocalServiceDao
import com.example.finalprojectapp.data.model.Credentials
import com.example.finalprojectapp.data.model.DataSet
import com.example.finalprojectapp.data.model.Service
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

class DataSetAdapter(
    private val localServiceDAO: LocalServiceDao,
    val packageName: String,
    private val coroutineScope: CoroutineScope
) {

    suspend fun getDataAsync(): Deferred<Service?> {
        return coroutineScope.async {
            localServiceDAO.publicGetServiceByName(packageName)
        }
    }

    fun generatesServiceClass(clientViewSaveData: MutableList<AutoFillNodeData>): Service {
        val credentialsList= mutableListOf<Credentials>()
        clientViewSaveData.forEach {
            if (!it.autofillHints.isNullOrEmpty()) {
                if (it.textValue != null)
                    credentialsList.add(
                        Credentials(it.autofillHints?.toList()!!, it.textValue!!)
                    )
                if (it.dataValue != null)
                    credentialsList.add(
                        Credentials(
                            it.autofillHints?.toList()!!,
                            it.dataValue!!.toString()))
            }
        }

        val dataSet= listOf(
            DataSet(
                credentials = credentialsList,dataSetName = chooseNameDataSet(clientViewSaveData)
            )
        )
        return Service(
            name = packageName,
            dataSets = dataSet
        )
    }

    private fun chooseNameDataSet(dataSet: List<AutoFillNodeData>): String {
        val hashLocal = mutableMapOf<String, String>()
        dataSet.forEach {
            it.autofillHints?.map { hint-> it.textValue?.let { it1 -> hashLocal.put(hint, it1) } }
            it.autofillHints?.map { hint-> it.dataValue?.let { it1 -> hashLocal.put(hint, it1.toString()) } }
        }
        return priority(hashLocal)
    }

    private fun priority(hashLocal: MutableMap<String, String>): String {
        return when {
            hashLocal[View.AUTOFILL_HINT_USERNAME]!=null -> hashLocal[View.AUTOFILL_HINT_USERNAME]!!
            hashLocal[View.AUTOFILL_HINT_EMAIL_ADDRESS]!=null -> hashLocal[View.AUTOFILL_HINT_EMAIL_ADDRESS]!!
            hashLocal[View.AUTOFILL_HINT_NAME]!=null -> hashLocal[View.AUTOFILL_HINT_NAME]!!
            hashLocal[View.AUTOFILL_HINT_PHONE]!=null -> hashLocal[View.AUTOFILL_HINT_PHONE]!!
            hashLocal[View.AUTOFILL_HINT_CREDIT_CARD_EXPIRATION_DATE]!=null -> hashLocal[View.AUTOFILL_HINT_CREDIT_CARD_EXPIRATION_DATE]!!
            else -> "SecretDataSet"
        }
    }

}