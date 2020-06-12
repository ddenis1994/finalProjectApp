package com.example.finalprojectapp.autoFillService

import android.app.assist.AssistStructure
import android.os.CancellationSignal
import android.service.autofill.*
import com.example.finalprojectapp.autoFillService.adapters.DataSetAdapter
import com.example.finalprojectapp.autoFillService.adapters.ResponseAdapter
import com.example.finalprojectapp.autoFillService.di.DaggerAutoFIllServiceComponent
import com.example.finalprojectapp.credentialsDB.NotificationRepository
import com.example.finalprojectapp.credentialsDB.ServiceRepository
import com.example.finalprojectapp.credentialsDB.ServiceRepositoryLocal
import com.example.finalprojectapp.data.autoFilleService.ClientViewMetadataBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject


class AutoFillService : AutofillService() {


    @Inject
    lateinit var mainRepository: ServiceRepository
    @Inject
    lateinit var mainRepositoryLocal: ServiceRepositoryLocal
    @Inject
    lateinit var notificationRepository: NotificationRepository
    @Inject
    lateinit var coroutineScope: CoroutineScope
    private lateinit var clientViewMetadata: List<AutofillFieldMetadata>
    @Inject
    lateinit var dataSetAdapter: DataSetAdapter
    private lateinit var responseAdapter: ResponseAdapter
    private lateinit var clientViewSaveData: MutableList<AutoFillNodeData>


    override fun onFillRequest(
        request: FillRequest, cancellationSignal: CancellationSignal,
        callback: FillCallback
    ) {
        val context: List<FillContext> = request.fillContexts
        val structure: AssistStructure = context[context.size - 1].structure

        DaggerAutoFIllServiceComponent.factory()
            .create(this, structure.activityComponent.packageName).inject(this)

        val newParserV2 = ClientParser(structure)
        clientViewMetadata =
            ClientViewMetadataBuilder(
                newParserV2
            ).buildClientViewMetadata()

        responseAdapter = ResponseAdapter(
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
        DaggerAutoFIllServiceComponent.factory()
            .create(this, structure.activityComponent.packageName).inject(this)
        val newParserV2 = ClientParser(structure)
        clientViewSaveData =
            ClientViewMetadataBuilder(
                newParserV2
            ).buildClientSaveMetadata()

        val service = dataSetAdapter.generatesServiceClass(clientViewSaveData)

        mainRepository.addService(service, callback)

    }


}