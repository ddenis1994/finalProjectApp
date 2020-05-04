package com.example.finalprojectapp.autoFillService

import android.app.assist.AssistStructure
import android.os.CancellationSignal
import android.service.autofill.*
import com.example.finalprojectapp.autoFillService.adapters.DataSetAdapter
import com.example.finalprojectapp.autoFillService.adapters.ResponseAdapter
import com.example.finalprojectapp.data.autoFilleService.ClientViewMetadataBuilder
import com.example.finalprojectapp.credentialsDB.CredentialsDataBase
import com.example.finalprojectapp.credentialsDB.LocalServiceDao
import com.example.finalprojectapp.data.model.Service
import com.example.finalprojectapp.crypto.Cryptography
import com.example.finalprojectapp.data.ServiceRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import java.security.MessageDigest
import java.util.*


class AutoFillService : AutofillService() {

    private lateinit var localServiceDAO: LocalServiceDao
    private lateinit var mainRepository: ServiceRepository
    private lateinit var coroutineScope: CoroutineScope
    private lateinit var clientViewMetadata: List<AutofillFieldMetadata>
    private lateinit var dataSetAdapter: DataSetAdapter
    private lateinit var responseAdapter: ResponseAdapter
    private lateinit var clientViewSaveData: MutableList<AutoFillNodeData>


    override fun onCreate() {
        super.onCreate()
        coroutineScope=CoroutineScope(Job())
        localServiceDAO =CredentialsDataBase.getDatabase(this.applicationContext).serviceDao()
        mainRepository= ServiceRepository.getInstance(localServiceDAO,applicationContext)

    }

    override fun onFillRequest(request: FillRequest, cancellationSignal: CancellationSignal,
                               callback: FillCallback) {
        val context: List<FillContext> = request.fillContexts
        val structure: AssistStructure = context[context.size - 1].structure

        val newParserV2=ClientParser(structure)
        clientViewMetadata=
            ClientViewMetadataBuilder(
                newParserV2
            ).buildClientViewMetadata()
        dataSetAdapter= DataSetAdapter(
                localServiceDAO,
                structure.activityComponent.packageName,
                coroutineScope)
        responseAdapter= ResponseAdapter(
            this.applicationContext,
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
            clientViewSaveData =
                ClientViewMetadataBuilder(
                    newParserV2
                ).buildClientSaveMetadata()
            dataSetAdapter= DataSetAdapter(
                localServiceDAO,
                structure.activityComponent.packageName,
                coroutineScope)
            val service=dataSetAdapter.generatesServiceClass(clientViewSaveData)
            GlobalScope.launch {
                mainRepository.addService(service,callback)
            }
        }


}