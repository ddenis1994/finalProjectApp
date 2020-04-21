package com.example.finalprojectapp.autoFillService.adapters

import android.R
import android.content.Context
import android.os.CancellationSignal
import android.service.autofill.Dataset
import android.service.autofill.FillCallback
import android.service.autofill.FillResponse
import android.service.autofill.SaveInfo
import android.view.View
import android.view.autofill.AutofillValue
import android.widget.RemoteViews
import com.example.finalprojectapp.autoFillService.AutofillFieldMetadata
import com.example.finalprojectapp.data.model.Credentials
import com.example.finalprojectapp.data.model.Service
import java.security.SecureRandom

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
        if (service != null)
            withLocalData(service,responseBuilder)
        else
            withNoData(responseBuilder,dataSetAdapter.packageName)

        val saveInfo = SaveInfo.Builder(clientViewMetadata.map { it.autofillType }
            .reduce { acc, num -> acc or num },
            clientViewMetadata.map { it.autofillId }.toTypedArray()
        ).build()
        responseBuilder.setSaveInfo(saveInfo)

        callback.onSuccess(responseBuilder.build())
    }

    private fun withLocalData(service:Service, fillResponse: FillResponse.Builder) {
        val hashLocal = mutableMapOf<String, Credentials>()
        service.dataSets?.forEach {
            val dataSet = Dataset.Builder()
            it.credentials?.forEach { cre ->
                cre.hint.map { hint -> hashLocal.put(hint, cre) }
            }
            clientViewMetadata.forEach { meta ->
                val presentation = RemoteViews(service.name, R.layout.simple_list_item_1)
                if (meta.isFocused)
                    presentation.setTextViewText(R.id.text1, hashLocal["username"]?.data)
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
    }


    private fun withNoData( fillResponse: FillResponse.Builder,service:String) {
        clientViewMetadata.forEach { meta->
            meta.autofillHints.forEach {
                if (it==View.AUTOFILL_HINT_PASSWORD) {
                    val recommendedPassword=generatePassword()
                    val presentation = RemoteViews(service, R.layout.simple_list_item_1)
                    presentation.setTextViewText(R.id.text1,recommendedPassword)
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


    private fun generatePassword() : String {

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
            val randomInt : Int = rnd.nextInt(result.length)
            sb.append(result[randomInt])
            i++
        }

        return sb.toString()
    }

    /**
     * Evaluate a random password
     * @param passwordToTest String with the password to test
     * @return a number from 0 to 1, 0 is a very bad password and 1 is a perfect password
     */
    fun evaluatePassword(passwordToTest: String) : Float {

        var factor = 0
        val length = passwordToTest.length

        if( passwordToTest.matches( Regex(".*[abcdefghijklmnopqrstuvwxyz].*") ) ) { factor += 2 }
        if( passwordToTest.matches( Regex(".*[ABCDEFGHIJKLMNOPQRSTUVWXYZ].*") ) ){ factor += 2 }
        if( passwordToTest.matches( Regex(".*[0123456789].*") ) ){ factor += 1 }
        if( passwordToTest.matches( Regex(".*[@#=+!£\$%&?].*") ) ){ factor += 5 }

        return (factor*length)/(10F*20F)
    }


}
