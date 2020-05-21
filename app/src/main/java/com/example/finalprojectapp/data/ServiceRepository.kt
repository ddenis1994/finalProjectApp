package com.example.finalprojectapp.data

import android.content.Context
import android.service.autofill.SaveCallback
import com.example.finalprojectapp.credentialsDB.LocalServiceDao
import com.example.finalprojectapp.crypto.Cryptography
import com.example.finalprojectapp.data.model.Service
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.util.*

class ServiceRepository private constructor(
    private val credentialsDao: LocalServiceDao,
    private val context:Context
) {

    suspend fun nukeALl(): Unit {
        credentialsDao.nukeALl()
    }

    suspend fun addService(
        service: Service,
        callback: SaveCallback
    ){
        withContext(Dispatchers.IO) {
            credentialsDao.publicInsertService(service)
            addDataToRemoteWithSaveCallBack(service, callback)
        }
    }



    companion object {
        @Volatile private var instance: ServiceRepository? = null
        fun getInstance(credentialsDao: LocalServiceDao,context: Context) =
            instance ?: synchronized(this) {
                instance ?: ServiceRepository(credentialsDao,context)
                        .also { instance = it }
            }
    }

    private fun addDataToRemoteWithSaveCallBack(
        service: Service,
        callback: SaveCallback
    ) {
        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser!!
        db.collection("users").document(user.uid)
            .collection("services").document(service.name)
            .set(service.copy(dataSets = null))
            .addOnSuccessListener {
                val cry= Cryptography(context)
                service.dataSets?.forEach {dataSet->
                    var rawData = dataSet.hashData
                    if (rawData.isNullOrEmpty()) {
                        rawData = String()
                        dataSet.credentials.let {
                            it?.forEach { cre ->
                                rawData += cre.data
                                rawData += cre.hint
                            }
                        }
                        val message: ByteArray = rawData.toByteArray()
                        val md = MessageDigest.getInstance("SHA-256")
                        rawData= Base64.getEncoder().encodeToString(md.digest(message))
                    }
                    val toUpload=cry.remoteEncryption(dataSet.copy(hashData = rawData))!!
                    db.collection("users").document(user.uid)
                        .collection("services").document(service.name)
                        .collection("dataSets").document(toUpload.hashData!!)
                        .set(toUpload)
                        .addOnSuccessListener {
                            callback.onSuccess()
                        }
                }
            }
    }
}