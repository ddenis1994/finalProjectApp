package com.example.finalprojectapp.autoFillService.adapters

import android.R
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.SharedPreferences
import android.os.CancellationSignal
import android.service.autofill.Dataset
import android.service.autofill.FillCallback
import android.service.autofill.FillResponse
import android.service.autofill.SaveInfo
import android.view.View
import android.view.autofill.AutofillId
import android.view.autofill.AutofillValue
import android.widget.RemoteViews
import com.example.finalprojectapp.autoFillService.AutofillFieldMetadata
import com.example.finalprojectapp.data.model.Credentials
import com.example.finalprojectapp.data.model.Service
import com.example.finalprojectapp.ui.auth.ServiceAuthActivity
import java.security.SecureRandom
import javax.inject.Inject

class ResponseAdapter @Inject constructor(
    private val context: Context,
    private val dataSetAdapter: DataSetAdapter,
    private val setting: SharedPreferences
) {

    private lateinit var  clientViewMetadata: List<AutofillFieldMetadata>
    private lateinit var callback: FillCallback
    private lateinit var  cancellationSignal: CancellationSignal

    fun setData(
        clientViewMetadata: List<AutofillFieldMetadata>,
        callback: FillCallback,
        cancellationSignal: CancellationSignal
    ) {
        this.clientViewMetadata=clientViewMetadata
        this.callback=callback
        this.cancellationSignal=cancellationSignal
    }

    suspend fun buildResponse() {
        if (clientViewMetadata.isNullOrEmpty())
            cancellationSignal.cancel()
        val responseBuilder = FillResponse.Builder()
        val service = dataSetAdapter.getDataAsync().await()
        if (service != null)
            withLocalData(service, responseBuilder)
        else
            dataSetAdapter.packageName?.let { withNoData(responseBuilder, it) }

        if (clientViewMetadata.isNotEmpty() && !clientViewMetadata.isNullOrEmpty()) {
            val saveInfo = SaveInfo.Builder(
                clientViewMetadata.map { it.autofillType }
                .reduce { acc, num -> acc or num },
                clientViewMetadata.map { it.autofillId }.toTypedArray()
            ).build()
            responseBuilder.setSaveInfo(saveInfo)

            callback.onSuccess(responseBuilder.build())
        } else
            callback.onFailure("cannot find hints")
    }

    private fun withLocalData(service: Service, fillResponse: FillResponse.Builder) {
        val secondFactor = setting.getString("SecondFactorAuthentication", "")
        if (!secondFactor.isNullOrBlank() && secondFactor != "None")
            fillResponseWith2Factor(service, fillResponse)
        else
            normalResponse(service, fillResponse)

    }

    private fun normalResponse(service: Service, fillResponse: FillResponse.Builder) {
        val hashLocal = mutableMapOf<String, Credentials>()
        service.dataSets?.forEach {
            var toSave = false
            val dataSet = Dataset.Builder()
            it.credentials?.forEach { cre ->
                cre.hint.map { hint -> hashLocal.put(hint, cre) }
            }
            clientViewMetadata.forEach { meta ->
                val presentation = RemoteViews(service.name, R.layout.simple_list_item_1)
                presentation.setTextViewText(R.id.text1, it.dataSetName)
                meta.autofillHints.map { hint ->
                    if (hashLocal[hint] != null) {
                        toSave = true
                        dataSet.setValue(
                            meta.autofillId,
                            AutofillValue.forText(hashLocal[hint]?.data),
                            presentation
                        )
                    }
                }
            }

            if (toSave)
                fillResponse.addDataset(dataSet.build())
        }
    }


    private fun normalResponseMake(service: Service): FillResponse {
        val fillResponse = FillResponse.Builder()
        val hashLocal = mutableMapOf<String, Credentials>()
        service.dataSets?.forEach {
            val dataSet = Dataset.Builder()
            it.credentials?.forEach { cre ->
                cre.hint.map { hint -> hashLocal.put(hint, cre) }
            }
            clientViewMetadata.forEach { meta ->
                val presentation = RemoteViews(service.name, R.layout.simple_list_item_1)
                presentation.setTextViewText(R.id.text1, it.dataSetName)
                meta.autofillHints.map { hint ->
                    dataSet.setValue(
                        meta.autofillId,
                        AutofillValue.forText(hashLocal[hint]?.data),
                        presentation
                    )
                }
            }
            fillResponse.addDataset(dataSet.build())
        }
        return fillResponse.build()
    }

    private fun fillResponseWith2Factor(service: Service, fillResponse: FillResponse.Builder) {
        val normalResponseMake = normalResponseMake(service)
        val authPresentation = RemoteViews(service.name, R.layout.simple_list_item_1).apply {
            setTextViewText(R.id.text1, "requires authentication")
        }

        val authIntent = Intent(context, ServiceAuthActivity::class.java).apply {
            putExtra("response", normalResponseMake)
        }
        val intentSender: IntentSender = PendingIntent.getActivity(
            context,
            1001,
            authIntent,
            PendingIntent.FLAG_CANCEL_CURRENT
        ).intentSender

        val list = mutableListOf<AutofillId>()
        clientViewMetadata.forEach { meta ->
            list.add(meta.autofillId)
        }

        fillResponse.setAuthentication(list.toTypedArray(), intentSender, authPresentation)

    }


    private fun withNoData(fillResponse: FillResponse.Builder, service: String) {
        clientViewMetadata.forEach { meta ->
            meta.autofillHints.forEach {
                if (it == View.AUTOFILL_HINT_PASSWORD) {
                    val recommendedPassword = generatePassword()
                    val presentation = RemoteViews(service, R.layout.simple_list_item_1)
                    presentation.setTextViewText(R.id.text1, recommendedPassword)
                    val dataSet = Dataset.Builder()
                    dataSet.setValue(
                        meta.autofillId,
                        AutofillValue.forText(recommendedPassword),
                        presentation
                    )
                    fillResponse.addDataset(dataSet.build())
                }
            }
        }


    }


    private fun generatePassword(): String {

        var result = ""
        var i = 0
        val length = 10

        result += "abcdefghijklmnopqrstuvwxyz"
        result += "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        result += "0123456789"
        result += "@#=+!£$%&?"

        val rnd = SecureRandom.getInstance("SHA1PRNG")

        val sb = StringBuilder(length)

        while (i < length) {
            val randomInt: Int = rnd.nextInt(result.length)
            sb.append(result[randomInt])
            i++
        }

        return sb.toString()
    }




}
