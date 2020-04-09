package com.example.finalprojectapp.autoFillService
import android.app.assist.AssistStructure
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.finalprojectapp.crypto.CredentialEncrypt
import com.example.finalprojectapp.data.model.Credentials
import com.example.finalprojectapp.data.model.ServiceCredentialsServer
import com.example.finalprojectapp.utils.SingleEncryptedSharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects


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
            val db = FirebaseFirestore.getInstance()
            val user = FirebaseAuth.getInstance().currentUser!!
            val sharedPreferences= SingleEncryptedSharedPreferences().getSharedPreference(context)
            Log.i("test12","make request")
            db.collection("users").document(sharedPreferences.getString("userDoc","none")!!)
                .collection("services")
                .whereEqualTo("name",requestClientPackage)
                .get()
                .addOnSuccessListener {documentSnapshot ->
                    Log.i("test12","got data")
                    if (documentSnapshot.documents.isEmpty())
                        falseResult.postValue(false)
                    else
                    documentSnapshot.documents.forEach {data->
                        val city = data.toObject<ServiceCredentialsServer>()
                        val encrypted= CredentialEncrypt("password")

                        result=encrypted.decryptAll(city!!.credentials)
                        falseResult.postValue(true)
                    }

                }
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