package com.example.finalprojectapp.autoFillService
import android.app.assist.AssistStructure
import androidx.lifecycle.MutableLiveData
import com.example.finalprojectapp.autoFillService.model.FilledAutofillFieldCollection
import com.example.finalprojectapp.crypto.CredentialEncrypt
import com.example.finalprojectapp.data.model.Credentials
import com.example.finalprojectapp.data.model.ServiceCredentialsServer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject


class ClientParser (private val requestClient:AssistStructure){
    // db variables
    private val db = FirebaseFirestore.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser

    private val requestClientPackage:String = requestClient.activityComponent.packageName
    private val nodesCount=requestClient.windowNodeCount

    val autoFillFields = AutofillFieldMetadataCollection()
    private var filledAutoFillFieldCollection: FilledAutofillFieldCollection = FilledAutofillFieldCollection()

    val autoFillDataForSaveList= mutableListOf<AutoFillNodeData>()

    var result=mutableListOf<Credentials>()

    val falseResult: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

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
            if (user != null)
                db.collection("users").document(user.uid)
                    .collection("services").document(requestClientPackage)
                    .get()
                    .addOnSuccessListener {
                        if(it.data.isNullOrEmpty())
                            falseResult.postValue(false)
                        else {
                            val city = it.toObject<ServiceCredentialsServer>()
                            val encrypted= CredentialEncrypt("password")
                            if (city != null)
                                result = encrypted.decryptAll(city.credentials)
                        }
                        filledAutoFillFieldCollection = FilledAutofillFieldCollection()
                        //mast have the parser in hare because this is call back
                        for (i in 0 until this.nodesCount)
                            parseNodeWindows(forFill, requestClient.getWindowNodeAt(i).rootViewNode)
                        falseResult.postValue(result.isNotEmpty())
                    }
        }
        else
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
/*
        if(forFill)
            node.autofillHints?.let { autoFillHints ->
                if (autoFillHints.isNotEmpty()) {
                    val result=AutoFillNodeData(node)
                    autoFillDataForSaveList.add(result)
                }
        }
        else {
            filledAutoFillFieldCollection.add(FilledAutofillField(node))
        }

 */
    }



}