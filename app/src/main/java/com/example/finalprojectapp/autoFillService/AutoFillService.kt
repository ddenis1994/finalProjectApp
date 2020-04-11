@file:Suppress("DEPRECATION")

package com.example.finalprojectapp.autoFillService

import android.R
import android.annotation.SuppressLint
import android.app.assist.AssistStructure
import android.os.CancellationSignal
import android.service.autofill.*
import android.util.Log
import android.view.autofill.AutofillValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
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

        val myParser= ClientParser(structure)
        myParser.parseForFill()

        val autoFillFields = myParser.autoFillFields
        myParser.falseResult.observe(this, androidx.lifecycle.Observer {
            val responseBuilder = FillResponse.Builder()
            if(it==true) {
                val presentation = AutofillHelper
                        .newRemoteViews(packageName, "tap to sing in", R.drawable.ic_lock_lock)
                val dataSet = Dataset.Builder()
                autoFillFields.allAutofillHints.forEachIndexed { index, hint ->
                    myParser.result.forEach { cre ->
                        cre.hint.forEach {hint2->
                            if (hint == hint2)
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
            else  if(autoFillFields.autofillIds.size>0) {

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

            val parser=ClientParser(structure)
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