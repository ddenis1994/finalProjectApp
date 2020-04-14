package com.example.finalprojectapp.autoFillService
import android.app.assist.AssistStructure
import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.finalprojectapp.data.model.Service
import com.example.finalprojectapp.localDB.PasswordRoomDatabase


class ClientParser(
    private val requestClient: AssistStructure,
    private val context: Context
){

    private val requestClientPackage:String = requestClient.activityComponent.packageName
    private val nodesCount=requestClient.windowNodeCount


    val autoFillFields = AutofillFieldMetadataCollection()

    val autoFillDataForSaveList= mutableListOf<AutoFillNodeData>()

    private fun getCredentials() =
        PasswordRoomDatabase.
        getDatabase(context).
        localCredentialsDAO().
        searchServiceCredentialsPublic(requestClientPackage)

        var  result=MutableLiveData<Service?>()

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
                result = getCredentials() as MutableLiveData<Service?>
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



