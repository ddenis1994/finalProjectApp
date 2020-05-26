package com.example.finalprojectapp.autoFillService

import android.app.assist.AssistStructure
import android.os.CancellationSignal
import android.service.autofill.*
import com.example.finalprojectapp.autoFillService.adapters.DataSetAdapter
import com.example.finalprojectapp.autoFillService.adapters.ResponseAdapter
import com.example.finalprojectapp.credentialsDB.LocalDataBase
import com.example.finalprojectapp.credentialsDB.NotificationRepository
import com.example.finalprojectapp.credentialsDB.ServiceRepository
import com.example.finalprojectapp.data.autoFilleService.ClientViewMetadataBuilder
import com.example.finalprojectapp.data.model.Notification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.format.DateTimeFormatter


class AutoFillService : AutofillService() {


    private lateinit var mainRepository: ServiceRepository
    private lateinit var notificationRepository: NotificationRepository
    private lateinit var coroutineScope: CoroutineScope
    private lateinit var clientViewMetadata: List<AutofillFieldMetadata>
    private lateinit var dataSetAdapter: DataSetAdapter
    private lateinit var responseAdapter: ResponseAdapter
    private lateinit var clientViewSaveData: MutableList<AutoFillNodeData>


    override fun onCreate() {
        super.onCreate()
        coroutineScope=CoroutineScope(Job())
        mainRepository= ServiceRepository.getInstance(applicationContext)
        notificationRepository=NotificationRepository.getInstance(LocalDataBase.getDatabase(this.applicationContext).notificationDao())

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
                mainRepository,
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
                mainRepository,
                structure.activityComponent.packageName,
                coroutineScope)
            val service=dataSetAdapter.generatesServiceClass(clientViewSaveData)
            GlobalScope.launch {
                mainRepository.addService(service,callback)
                notificationRepository.insert(Notification(0,"Inserted Credentials",service.name,DateTimeFormatter.ISO_INSTANT.format(Instant.now())))
            }
        }


}