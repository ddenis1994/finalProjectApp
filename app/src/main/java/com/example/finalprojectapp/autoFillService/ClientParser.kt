package com.example.finalprojectapp.autoFillService
import android.app.assist.AssistStructure
import android.content.Context
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.example.finalprojectapp.autoFillService.model.FilledAutofillFieldCollection
import com.example.finalprojectapp.crypto.CredentialEncrypt
import com.example.finalprojectapp.data.model.Credentials
import com.example.finalprojectapp.data.model.Service
import com.example.finalprojectapp.localDB.LocalCredentialsDAO
import com.example.finalprojectapp.localDB.PasswordRoomDatabase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext


class ClientParser (
    private val requestClient:AssistStructure,
    private val context:Context,
    private val coroutineScope: LifecycleCoroutineScope
){

    private val requestClientPackage:String = requestClient.activityComponent.packageName
    private val nodesCount=requestClient.windowNodeCount


    val autoFillFields = AutofillFieldMetadataCollection()

    val autoFillDataForSaveList= mutableListOf<AutoFillNodeData>()

    suspend fun getCredentials() =
        PasswordRoomDatabase.
        getDatabase(context).
        localCredentialsDAO().
        searchServiceCredentialsPublic(requestClientPackage)

        var  result=MutableLiveData<List<Service>>()

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
                        result = getCredentials() as MutableLiveData<List<Service>>
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



