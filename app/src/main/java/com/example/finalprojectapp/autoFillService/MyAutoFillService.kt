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


class MyAutoFillService : AutofillService() , LifecycleOwner {

    private lateinit var lifecycleRegistry: LifecycleRegistry

    override fun onFillRequest(request: FillRequest, cancellationSignal: CancellationSignal,
                               callback: FillCallback) {
        val context: List<FillContext> = request.fillContexts
        val structure: AssistStructure = context[context.size - 1].structure

        val parser = StructureParser(structure)
        parser.parseForFill()
        val autofillFields = parser.autofillFields

        val myParser= MyClientParser(structure)
        myParser.parseForSave(true,this)
        myParser.falseResult.observe(this, androidx.lifecycle.Observer {
            val responseBuilder = FillResponse.Builder()
            if(it==true) {
                val presentation = AutofillHelper
                        .newRemoteViews(packageName, "tap to sing in", R.drawable.ic_lock_lock)
                val dataset = Dataset.Builder()
                val h=myParser.result
                autofillFields.allAutofillHints.forEachIndexed { index, hint ->
                    myParser.result.forEach { cre ->
                        cre.hint.forEach {hint2->
                            if (hint == hint2)
                                dataset.setValue(
                                    autofillFields.autofillIds[index],
                                    AutofillValue.forText(cre.data),
                                    presentation
                                )
                        }

                    }
                }
                responseBuilder.addDataset(dataset.build())
            }
            else  if(autofillFields.autofillIds.size>0) {

                responseBuilder.setSaveInfo(
                        SaveInfo.Builder(
                                SaveInfo.SAVE_DATA_TYPE_USERNAME or SaveInfo.SAVE_DATA_TYPE_PASSWORD or SaveInfo.SAVE_DATA_TYPE_EMAIL_ADDRESS,
                                autofillFields.autofillIds.toTypedArray()
                        ).build())
            }
            if(autofillFields.autofillIds.size>0)
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

            val parser=MyClientParser(structure)
            parser.parseForSave(false,this)

            val gson=Gson().toJson(parser.autoFillDataForSaveList)
            parser.autoFillDataForSaveList.forEach{
                it.autofillHints?.forEach { hint ->

                }
            }
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