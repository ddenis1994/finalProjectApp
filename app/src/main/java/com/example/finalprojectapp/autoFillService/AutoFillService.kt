@file:Suppress("DEPRECATION")

package com.example.finalprojectapp.autoFillService

import android.app.assist.AssistStructure
import android.os.CancellationSignal
import android.service.autofill.*
import com.example.finalprojectapp.autoFillService.adapters.DataSetAdapter
import com.example.finalprojectapp.autoFillService.adapters.ResponseAdapter
import com.example.finalprojectapp.autoFillService.data.ClientViewMetadataBuilder
import com.example.finalprojectapp.credentialsDB.CredentialsDataBase
import com.example.finalprojectapp.credentialsDB.LocalServiceDao
import com.example.finalprojectapp.credentialsDB.model.Service
import com.example.finalprojectapp.crypto.Cryptography
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import java.security.MessageDigest
import java.util.*


class AutoFillService : AutofillService() {

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

        val newParserV2=ClientParser(structure)
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


        }

        override fun onSaveRequest(request: SaveRequest, callback: SaveCallback) {
            val context = request.fillContexts
            val structure = context[context.size - 1].structure
            val newParserV2=ClientParser(structure)
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
            localServiceDAO.publicInsertService(service)
            addDataToRemote(service)
            callback.onSuccess()
        }


    }


        private fun addDataToRemote(service: Service) {
            val db = FirebaseFirestore.getInstance()
            val user = FirebaseAuth.getInstance().currentUser!!
            db.collection("users").document(user.uid)
                .collection("services").document(service.name)
                .set(service.copy(dataSets = null))
                .addOnSuccessListener {
                    val cry=Cryptography(this)
                    service.dataSets?.forEach {dataset->
                        var rawData = dataset.hashData
                        if (rawData.isNullOrEmpty()) {
                            rawData = String()
                            dataset.credentials.let {
                                it?.forEach { cre ->
                                    rawData += cre.data
                                    rawData += cre.hint
                                }
                            }
                            val message: ByteArray = rawData.toByteArray()
                            val md = MessageDigest.getInstance("SHA-256")
                            rawData= Base64.getEncoder().encodeToString(md.digest(message))
                        }
                        val toUpload=cry.remoteEncryption(dataset.copy(hashData = rawData))
                        db.collection("users").document(user.uid)
                            .collection("services").document(service.name)
                            .collection("dataSets").document(toUpload.hashData!!)
                            .set(toUpload)
                    }
                }
        }

}