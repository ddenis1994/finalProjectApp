package com.example.finalprojectapp.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.finalprojectapp.autoFillService.AutoFillNodeData
import com.example.finalprojectapp.crypto.CredentialEncrypt
import com.example.finalprojectapp.data.model.ServiceCredentialsServer
import com.google.common.reflect.TypeToken
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
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
        /*
        val settings = FirebaseFirestoreSettings.Builder().apply {
            host="10.0.2.2:8080"
            isSslEnabled = false
            isPersistenceEnabled = false }
            .build()
        val db = FirebaseFirestore.getInstance()
        db.firestoreSettings = settings

         */

        val db = FirebaseFirestore.getInstance()

        val user = FirebaseAuth.getInstance().currentUser!!

        val imageUriInput = if(inputData.getString("data").isNullOrEmpty())
            ""
        else
            inputData.getString("data")!!


        val serviceName=inputData.getString("serviceRequest")!!
        val json= Gson()
        val mutableListTutorialType = object : TypeToken<MutableList<AutoFillNodeData>>() {}.type
        val credentialsData: MutableList<AutoFillNodeData> = json.fromJson(imageUriInput, mutableListTutorialType)

        db.collection("services")
            .whereEqualTo("userId", user.uid)
            .whereEqualTo("name",serviceName)
            .get()
            .addOnSuccessListener { query ->
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
                        serviceName,
                        Timestamp(Date()),
                        user.uid,
                        newCredentials)
                    data.credentials=encrypted.encryptAll(data.credentials)
                    db.collection("services").add(data)
                        .addOnFailureListener { e ->
                            Log.i("worker", e.toString())
                        }
                }
                else{
                    db.collection("services").document(query.documents[0].id)
                        .update("credentials",newCredentials)
                        .addOnFailureListener {e->
                            Log.i("worker", e.toString())
                        }
                }
            }
        Result.success()
    }

}
