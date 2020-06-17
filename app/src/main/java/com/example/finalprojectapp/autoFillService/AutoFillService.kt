package com.example.finalprojectapp.autoFillService

import android.app.assist.AssistStructure
import android.os.CancellationSignal
import android.service.autofill.*
import com.example.finalprojectapp.autoFillService.adapters.DataSetAdapter
import com.example.finalprojectapp.autoFillService.adapters.ResponseAdapter
import com.example.finalprojectapp.autoFillService.di.DaggerAutoFIllServiceComponent
import com.example.finalprojectapp.credentialsDB.ServiceRepository
import com.example.finalprojectapp.data.autoFilleService.ClientViewMetadataBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject


class AutoFillService : AutofillService() {


    @Inject
    lateinit var mainRepository: ServiceRepository
    @Inject
    lateinit var coroutineScope: CoroutineScope
    @Inject
    lateinit var dataSetAdapter: DataSetAdapter
    @Inject
    lateinit var  responseAdapter : ResponseAdapter

    private lateinit var clientViewMetadata: List<AutofillFieldMetadata>

    override fun onConnected() {
        DaggerAutoFIllServiceComponent.factory()
            .create(this).inject(this)
        super.onConnected()
    }



    private lateinit var clientViewSaveData: MutableList<AutoFillNodeData>


    override fun onFillRequest(
        request: FillRequest, cancellationSignal: CancellationSignal,
        callback: FillCallback
    ) {
        val context: List<FillContext> = request.fillContexts
        val structure: AssistStructure = context[context.size - 1].structure
        dataSetAdapter.packageName=structure.activityComponent.packageName


        val newParserV2 = ClientParser(structure)
        clientViewMetadata =
            ClientViewMetadataBuilder(
                newParserV2
            ).buildClientViewMetadata()

        responseAdapter.setData(
            clientViewMetadata,
            callback,
            cancellationSignal
        )
        coroutineScope.launch {
            responseAdapter.buildResponse()
        }
    }

    override fun onDisconnected() {
        super.onDisconnected()
        coroutineScope.cancel()
    }

    override fun onSaveRequest(request: SaveRequest, callback: SaveCallback) {
        val context = request.fillContexts
        val structure = context[context.size - 1].structure
        val newParserV2 = ClientParser(structure)
        dataSetAdapter.packageName=structure.activityComponent.packageName
        clientViewSaveData =
            ClientViewMetadataBuilder(
                newParserV2
            ).buildClientSaveMetadata()

        val service = dataSetAdapter.generatesServiceClass(clientViewSaveData)

        if (service != null) {
            GlobalScope.launch {
                mainRepository.addService(service, callback)
            }
        }

    }


}