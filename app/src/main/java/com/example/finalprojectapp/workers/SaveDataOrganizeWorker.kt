package com.example.finalprojectapp.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.example.finalprojectapp.autoFillService.AutoFillNodeData
import com.example.finalprojectapp.crypto.CredentialEncrypt
import com.example.finalprojectapp.data.model.ServiceCredentialsServer
import com.example.finalprojectapp.utils.SingleEncryptedSharedPreferences
import com.google.common.reflect.TypeToken
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlinx.coroutines.coroutineScope
import java.util.*


@Suppress("UnstableApiUsage")
class SaveDataOrganizeWorker(context: Context,
                             workerParams: WorkerParameters
)
    : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result = coroutineScope{

        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser!!

        val imageUriInput = inputData.getString("data")
        val serviceName=inputData.getString("serviceRequest")
        val testDataResult=checkData(serviceName, imageUriInput,user)
        if (testDataResult!=null)
            return@coroutineScope Result.failure(testDataResult)

        val mutableListTutorialType = object : TypeToken<MutableList<AutoFillNodeData>>() {}.type
        val credentialsData: MutableList<AutoFillNodeData> = Gson().fromJson(imageUriInput!!, mutableListTutorialType)

        val userDocumented:String?=getUserDoc()
            db.collection("users").document(userDocumented!!)
                .collection("services")
                .whereEqualTo("name",serviceName)
                .get()
                .addOnSuccessListener {query ->
                    val newCredentials = mutableListOf<Map<String,Any>>()
                    val encrypted= CredentialEncrypt("password")
                    credentialsData.forEach {
                        newCredentials.add(
                            hashMapOf(
                                "hint" to it.autofillHints!!.toList(),
                                "data" to it.textValue!!
                            )
                        )
                    }
                    if (query.isEmpty) {
                        val data = ServiceCredentialsServer(
                            serviceName!!,
                            Timestamp(Date()),
                            user.uid,
                            newCredentials)
                        data.credentials=encrypted.encryptAll(data.credentials)

                        db.collection("users").document(userDocumented)
                            .collection("services").add(data)
                            .addOnFailureListener { e ->
                                Log.i("worker", e.toString())
                            }
                    }
                    else{
                        db.collection("users").document(userDocumented)
                            .collection("services").document(query.documents[0].id)
                            .update("credentials",newCredentials)
                            .addOnFailureListener {e->
                                Log.i("worker", e.toString())
                            }
                }

        }




        Result.success()

    }

    private fun checkData(
        serviceName: String?,
        credentialsData: String?,
        user: FirebaseUser?
    ): Data? {
        if (user==null) {
            return createOutputData("not user signIn")
        }
        if(serviceName==null) {
            return createOutputData("cannot find service name")
        }
        if (credentialsData==null)
            return createOutputData("cannot find service data")
        return null

    }

    private fun getUserDoc(): String? {
        val sharedPreferences= SingleEncryptedSharedPreferences().getSharedPreference(applicationContext)
        return sharedPreferences.getString("userDoc","none")
    }

    private fun createOutputData(reasonData: String): Data {
        return Data.Builder()
            .putString("reason", reasonData)
            .build()
    }

}
