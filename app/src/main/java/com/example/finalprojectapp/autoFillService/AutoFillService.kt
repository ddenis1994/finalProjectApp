@file:Suppress("DEPRECATION")

package com.example.finalprojectapp.autoFillService

import android.R
import android.annotation.SuppressLint
import android.app.assist.AssistStructure
import android.content.Context
import android.os.CancellationSignal
import android.service.autofill.*
import android.util.Log
import android.view.autofill.AutofillValue
import androidx.lifecycle.*
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.finalprojectapp.workers.SaveDataOrganizeWorker
import com.google.gson.Gson


class AutoFillService : AutofillService() , LifecycleOwner {

    private lateinit var lifecycleRegistry: LifecycleRegistry

    override fun onFillRequest(request: FillRequest, cancellationSignal: CancellationSignal,
                               callback: FillCallback) {
        val context: List<FillContext> = request.fillContexts
        val structure: AssistStructure = context[context.size - 1].structure

        val myParser= ClientParser(structure,applicationContext as Context,lifecycleScope)

        myParser.parseForFill()

        val autoFillFields = myParser.autoFillFields

        myParser.result.observe(this, Observer {service ->
            val responseBuilder = FillResponse.Builder()
            if(service!=null) {
                val presentation = AutofillHelper
                        .newRemoteViews(packageName, "tap to sing in", R.drawable.ic_lock_lock)
                val dataSet = Dataset.Builder()
                autoFillFields.allAutofillHints.forEachIndexed { index, hintFromAndroid ->
                        service.credentials?.forEach {cre ->
                            cre.hint.forEach { hintFromLocalDB ->
                                if (hintFromAndroid == hintFromLocalDB)
                                    dataSet.setValue(
                                        autoFillFields.autofillIds[index],
                                        AutofillValue.forText(cre.data),
                                        presentation
                                    )
                        }
                    }
                }
                responseBuilder.addDataset(dataSet.build())
            }
            else if(autoFillFields.autofillIds.size>0) {
                responseBuilder.setSaveInfo(
                        SaveInfo.Builder(
                                SaveInfo.SAVE_DATA_TYPE_USERNAME or SaveInfo.SAVE_DATA_TYPE_PASSWORD or SaveInfo.SAVE_DATA_TYPE_EMAIL_ADDRESS,
                                autoFillFields.autofillIds.toTypedArray()
                        ).build())
            }
            if(autoFillFields.autofillIds.size>0)
                callback.onSuccess(responseBuilder.build())
            else
                callback.onFailure("cannot sort this out")
        })

        }

        @SuppressLint("RestrictedApi")
        override fun onSaveRequest(request: SaveRequest, callback: SaveCallback) {
            Log.i("autoFillService", "started auto fill service save process")
            val context = request.fillContexts
            val structure = context[context.size - 1].structure

            val parser=ClientParser(structure,applicationContext as Context,lifecycleScope)
            parser.parseForSave()

            val gson=Gson().toJson(parser.autoFillDataForSaveList)
            val dataToPass= Data.Builder()
                    .put("data",gson)
                    .put("serviceRequest",parser.packageClientName())
                    .build()
            val updateWorkRequest= OneTimeWorkRequestBuilder<SaveDataOrganizeWorker>()
                    .setInputData(dataToPass)
                    .build()
            WorkManager.getInstance(applicationContext).enqueue(updateWorkRequest)
            callback.onSuccess()
        }

    override fun getLifecycle(): Lifecycle {
        return lifecycleRegistry
    }

    override fun onConnected() {
        lifecycleRegistry = LifecycleRegistry(this)
        lifecycleRegistry.markState(Lifecycle.State.CREATED)
        lifecycleRegistry.markState(Lifecycle.State.STARTED)
    }

    override fun onDisconnected() {
        lifecycleRegistry.markState(Lifecycle.State.DESTROYED)
    }

}