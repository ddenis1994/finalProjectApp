package com.example.finalprojectapp.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.example.finalprojectapp.autoFillService.AutoFillNodeData
import com.example.finalprojectapp.crypto.CredentialEncrypt
import com.example.finalprojectapp.data.model.Credentials
import com.example.finalprojectapp.data.model.Service
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
    override suspend fun doWork(): Result = coroutineScope {

        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser!!

        val imageUriInput = inputData.getString("data")
        val serviceName = inputData.getString("serviceRequest")
        val testDataResult = checkData(serviceName, imageUriInput, user)
        if (testDataResult != null)
            return@coroutineScope Result.failure(testDataResult)

        val mutableListTutorialType = object : TypeToken<MutableList<AutoFillNodeData>>() {}.type
        val credentialsData: MutableList<AutoFillNodeData> =
            Gson().fromJson(imageUriInput!!, mutableListTutorialType)

        val newCredentials = mutableListOf<Credentials>()
        val encrypted = CredentialEncrypt("password")
        credentialsData.forEach {
            newCredentials.add(
                Credentials(0,0,it.autofillHints!!.toList(), it.textValue!!, null, null)
            )
        }
        val data = Service(
            serviceName!!,
            Timestamp(Date()),
            user.uid,
            newCredentials.toList()
        )
        db.collection("users").document(user.uid)
            .collection("services").document(serviceName)
            .set(data.copy(credentials = encrypted.encryptAll(data.credentials)))

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


    private fun createOutputData(reasonData: String): Data {
        return Data.Builder()
            .putString("reason", reasonData)
            .build()
    }

}
