package com.example.finalprojectapp.autoFillService.adapters

import android.R
import android.content.Context
import android.os.CancellationSignal
import android.service.autofill.Dataset
import android.service.autofill.FillCallback
import android.service.autofill.FillResponse
import android.service.autofill.SaveInfo
import android.view.autofill.AutofillValue
import android.widget.RemoteViews
import com.example.finalprojectapp.autoFillService.AutofillFieldMetadata
import com.example.finalprojectapp.data.model.Credentials

class ResponseAdapter(
    private val context: Context,
    private val dataSetAdapter: DataSetAdapter,
    private val clientViewMetadata: List<AutofillFieldMetadata>,
    private val callback: FillCallback,
    private val cancellationSignal: CancellationSignal
) {
    suspend fun buildResponse() {
        if (clientViewMetadata.isNullOrEmpty())
            cancellationSignal.cancel()
        val responseBuilder = FillResponse.Builder()
        val service = dataSetAdapter.getDataAsync().await()
        if (service != null) {
            val hashLocal = mutableMapOf<String, Credentials>()
            service.dataSets?.forEach {
                val dataSet = Dataset.Builder()
                it.credentials?.forEach { cre ->
                    cre.hint.map { hint -> hashLocal.put(hint, cre) }
                }
                clientViewMetadata.forEach {meta->
                    val presentation = RemoteViews(service.name, R.layout.simple_list_item_1)
                    //TODO fix the data
                    if(meta.isFocused)
                        presentation.setTextViewText(R.id.text1, hashLocal["username"]?.data)
                    meta.autofillHints.map { hint ->

                        dataSet.setValue(
                                meta.autofillId,
                                AutofillValue.forText(hashLocal[hint]?.data),
                            presentation
                        )
                    }
                }
                responseBuilder.addDataset(dataSet.build())
            }
        }

        val saveInfo = SaveInfo.Builder(clientViewMetadata.map { it.autofillType }
            .reduce { acc, num -> acc or num },
            clientViewMetadata.map { it.autofillId }.toTypedArray()
        ).build()
        responseBuilder.setSaveInfo(saveInfo)

        callback.onSuccess(responseBuilder.build())
    }
}
