package com.example.finalprojectapp.autoFillService
import android.app.assist.AssistStructure
import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.finalprojectapp.data.model.Credentials


class MyClientParser (val requestClient:AssistStructure){
    private val requestClientPackage:String = requestClient.activityComponent.packageName
    private val nodesCount=requestClient.windowNodeCount

    val autoFillDataForSaveList= mutableListOf<AutoFillNodeData>()

    var result=mutableListOf<Credentials>()
    val falseResult: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    fun packageClientName(): String {
        return requestClientPackage
    }

    fun windowCount():Int{
        return nodesCount
    }
    fun parseForSave(boolean: Boolean,context: Context){
        parseClientForSave(boolean,context)
    }

    private fun parseClientForSave(forFill: Boolean, context:Context) {
        if(forFill) {
            falseResult.postValue(false)
            /*
            val database = PasswordRoomDatabase.getDatabase(context)
            val service =database.passwordsDAO().searchDB(this.packageClientName())
            service.observeForever {
                if (it.isEmpty()) {
                    falseResult.value = false

                }
                else {
                    var resultQuery = it[0].credentials
                    result.addAll(resultQuery)
                    falseResult.value = true
                }
            }

             */
        }
        else
            for (i in 0 until this.windowCount()) {
                parseNodeWindows(requestClient.getWindowNodeAt(i).rootViewNode)
            }
    }
    private fun parseNodeWindows(node: AssistStructure.ViewNode):Unit{
        if(node.childCount>0)
             for(i in 0 until node.childCount) parseNodeWindows(node.getChildAt(i))
        node.autofillHints?.let { autofillHints ->
            if (autofillHints.isNotEmpty()) {
                val result=AutoFillNodeData(node)
                autoFillDataForSaveList.add(result)
            }
        }
    }



}