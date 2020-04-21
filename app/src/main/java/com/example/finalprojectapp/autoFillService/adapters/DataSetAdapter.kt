package com.example.finalprojectapp.autoFillService.adapters

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
    private val packageName: String,
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
                        Credentials(
                            it.autofillHints?.toList()!!,
                            it.textValue!!
                        )
                    )
                if (it.dataValue != null)
                    credentialsList.add(
                        Credentials(
                            it.autofillHints?.toList()!!,
                            it.dataValue!!.toString()
                        )
                    )
            }
        }

        val dataSet= listOf(
            DataSet(
                credentials = credentialsList
            )
        )
        return Service(
            name = packageName,
            dataSets = dataSet
        )
    }

}