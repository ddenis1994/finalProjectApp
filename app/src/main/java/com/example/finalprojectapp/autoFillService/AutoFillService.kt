@file:Suppress("DEPRECATION")

package com.example.finalprojectapp.autoFillService

import android.annotation.SuppressLint
import android.app.assist.AssistStructure
import android.os.CancellationSignal
import android.service.autofill.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.example.finalprojectapp.autoFillService.adapters.DataSetAdapter
import com.example.finalprojectapp.autoFillService.adapters.ResponseAdapter
import com.example.finalprojectapp.autoFillService.data.ClientViewMetadataBuilder
import com.example.finalprojectapp.credentialsDB.CredentialsDataBase
import com.example.finalprojectapp.credentialsDB.LocalServiceDao
import com.example.finalprojectapp.credentialsDB.model.Service
import kotlinx.coroutines.*


class AutoFillService : AutofillService() , LifecycleOwner {

    private lateinit var lifecycleRegistry: LifecycleRegistry
    private lateinit var localServiceDAO: LocalServiceDao
    private lateinit var coroutineScope: CoroutineScope
    private lateinit var clientViewMetadata: List<AutofillFieldMetadata>
    private lateinit var dataSetAdapter: DataSetAdapter
    private lateinit var responseAdapter: ResponseAdapter
    private lateinit var clientViewSaveData: MutableList<AutoFillNodeData>


    override fun onCreate() {
        super.onCreate()

        coroutineScope=CoroutineScope(Job())
        localServiceDAO =CredentialsDataBase.getDatabase(this).credentialsDao()

    }

    override fun onFillRequest(request: FillRequest, cancellationSignal: CancellationSignal,
                               callback: FillCallback) {
        val context: List<FillContext> = request.fillContexts
        val structure: AssistStructure = context[context.size - 1].structure

        val newParserV2=ParserV2(structure)
        clientViewMetadata=ClientViewMetadataBuilder(newParserV2).buildClientViewMetadata()
        dataSetAdapter= DataSetAdapter(
                localServiceDAO,
                structure.activityComponent.packageName,
                coroutineScope)
        responseAdapter= ResponseAdapter(
            this,
            dataSetAdapter,
            clientViewMetadata,
            callback,
            cancellationSignal
        )

        coroutineScope.launch {
            responseAdapter.buildResponse()
        }

/*
        val myParser= ClientParser(structure, applicationContext as Context)


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

 */

        }

        @SuppressLint("RestrictedApi")
        override fun onSaveRequest(request: SaveRequest, callback: SaveCallback) {
            val context = request.fillContexts
            val structure = context[context.size - 1].structure
            val newParserV2=ParserV2(structure)
            clientViewSaveData =ClientViewMetadataBuilder(newParserV2).buildClientSaveMetadata()
            dataSetAdapter= DataSetAdapter(
                localServiceDAO,
                structure.activityComponent.packageName,
                coroutineScope)
            val service=dataSetAdapter.generatesServiceClass(clientViewSaveData)
            GlobalScope.launch {
                addDataToLocal(service,callback)
            }
        }
    private suspend fun addDataToLocal(
        service: Service,
        callback: SaveCallback){
        withContext(Dispatchers.IO){
            val result=localServiceDAO.publicInsertService(service)
            callback.onSuccess()
        }


    }
/*
    private suspend fun addDataToLocal(autoFillDataForSaveList: MutableList<AutoFillNodeData>,request:String) {
        val localDB=PasswordRoomDatabase.getDatabase(applicationContext)
        val listOfCredentials= mutableListOf<Credentials>()
        autoFillDataForSaveList.forEach {
            it.textValue?.let { it1 ->
                Credentials(it.autofillHints?.toList()!!,
                    it1
                )
            }?.let { it2 -> listOfCredentials.add(it2) }
        }
        if (listOfCredentials.isNotEmpty()) {
            val service = Service(request, "", null, null, listOfCredentials)
            val cryptography=Cryptography(applicationContext)
            cryptography.setService(service)

            cryptography.localEncrypt()?.let {
                localDB.localCredentialsDAO()
                    .insertSingleServiceCredentials(it)
            }



            cryptography.setService(service)
            cryptography.remoteEncryption()?.let { addDataToRemote(it) }
        }
    }

    private fun addDataToRemote(service: Service) {
        if(service.hashData!="") {
            val db = FirebaseFirestore.getInstance()
            val user = FirebaseAuth.getInstance().currentUser!!
            db.collection("users").document(user.uid)
                .collection("services").document(service.name)
                .set(service)
        }
    }


 */
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