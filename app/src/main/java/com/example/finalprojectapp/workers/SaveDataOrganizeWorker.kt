package com.example.finalprojectapp.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.finalprojectapp.autoFillService.AutoFillNodeData
import com.example.finalprojectapp.data.model.ServiceCredentialsServer
import com.example.finalstudy.crypto.CredentialEncrypt
import com.google.common.reflect.TypeToken
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import kotlinx.coroutines.coroutineScope
import java.util.*


class SaveDataOrganizeWorker(context: Context,
                             workerParams: WorkerParameters
)
    : CoroutineWorker(context, workerParams) {



    override suspend fun doWork(): Result = coroutineScope{

        val settings = FirebaseFirestoreSettings.Builder().apply {
            host="10.0.2.2:8080"
            isSslEnabled = false
            isPersistenceEnabled = false }
            .build()

        val db = FirebaseFirestore.getInstance()
        db.firestoreSettings = settings
        val auth: FirebaseAuth = FirebaseAuth.getInstance()

        val imageUriInput = inputData.getString("data")
        val serviceName=inputData.getString("serviceRequest")
        val json= Gson()
        val mutableListTutorialType = object : TypeToken<MutableList<AutoFillNodeData>>() {}.type
        val credentialsData: MutableList<AutoFillNodeData> = json.fromJson(imageUriInput, mutableListTutorialType)

        val encrypted= CredentialEncrypt("password")

        val data = ServiceCredentialsServer(
            serviceName,
            Timestamp(Date()),
            //TODO return the user id in production
            //auth.currentUser!!,
            mutableListOf())
        credentialsData.forEach{
                data.credentials.add(
                    hashMapOf(
                    "hint" to it.autofillHints!!.toList(),
                    "data" to it.textValue!!
                )
                )
            }

       db.collection("services").add(data)
        .addOnSuccessListener { decument->
            Log.i("worker",decument.id)
        }


        Result.success()
    }


}


/*
        val singleEncryptedSharedPreferences= SingleEncryptedSharedPreferences()
            .getSharedPreference(applicationContext)
        val password:String= singleEncryptedSharedPreferences.getString("password","").toString()
//old version with custom database
var rowNumber: Long = if(database.serviceDAO().isExsists(serviceName.toString())) {
    database.serviceDAO().getServicesKnow(serviceName.toString())[0].serviceId
}
else{
    database.serviceDAO().insert(
        Service(
            0,
            serviceName.toString()
        )
    )
}

//start to save the hints in the DB
credentials.forEach {
    it.autofillHints?.forEach {hint ->
        val tempCre: Credentials =Credentials(
            0,rowNumber, hint, it.textValue!!,
            "","")
        database.passwordsDAO().insert(
            encrypted.encrypt(tempCre)
        )
    }
}
//start new sync job


 */