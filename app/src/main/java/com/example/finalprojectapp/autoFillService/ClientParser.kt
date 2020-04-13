package com.example.finalprojectapp.autoFillService
import android.app.assist.AssistStructure
import android.content.Context
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.MutableLiveData
import com.example.finalprojectapp.data.model.Service
import com.example.finalprojectapp.localDB.PasswordRoomDatabase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ClientParser (
    private val requestClient:AssistStructure,
    private val context:Context,
    private val coroutineScope: LifecycleCoroutineScope
){

    private val requestClientPackage:String = requestClient.activityComponent.packageName
    private val nodesCount=requestClient.windowNodeCount


    val autoFillFields = AutofillFieldMetadataCollection()

    val autoFillDataForSaveList= mutableListOf<AutoFillNodeData>()

    private suspend fun getCredentials() =
        PasswordRoomDatabase.
        getDatabase(context).
        localCredentialsDAO().
        searchServiceCredentialsPublic(requestClientPackage)

        var  result=MutableLiveData<Service>()

        fun packageClientName(): String {
            return requestClientPackage
        }


        fun parseForSave(){
            parse(false)
        }

        fun parseForFill() {
            parse(true)
        }


        private fun parse(forFill: Boolean) {
            if(forFill) {
                coroutineScope.launch {
                    withContext(Dispatchers.IO) {
                        result = getCredentials() as MutableLiveData<Service>
                    }
                }
            }
            for (i in 0 until this.nodesCount)
                parseNodeWindows(forFill, requestClient.getWindowNodeAt(i).rootViewNode)
        }
        private fun parseNodeWindows(forFill: Boolean,node: AssistStructure.ViewNode) {
            node.autofillHints?.let { autoFillHints ->
                if (autoFillHints.isNotEmpty()) {
                    if (forFill) {
                        autoFillFields.add(AutofillFieldMetadata(node))
                    } else {
                        val result=AutoFillNodeData(node)
                        autoFillDataForSaveList.add(result)
                    }
                }
            }
            if(node.childCount>0)
                for(i in 0 until node.childCount) parseNodeWindows(forFill,node.getChildAt(i))

        }
    }



