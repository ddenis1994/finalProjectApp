package com.example.finalprojectapp.credentialsDB

import android.content.Context
import android.service.autofill.SaveCallback
import com.example.finalprojectapp.crypto.Cryptography
import com.example.finalprojectapp.data.model.DataSet
import com.example.finalprojectapp.data.model.Service
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.security.MessageDigest
import java.util.*

class ServiceRepositoryRemote(private val context: Context) {

    private val user = FirebaseAuth.getInstance().currentUser!!
    private val db = FirebaseFirestore.getInstance()


    internal fun addDataToRemoteWithSaveCallBack(
        service: Service,
        callback: SaveCallback
    ) {

        db.collection("users").document(user.uid)
            .collection("services").document(service.name)
            .set(service.copy(dataSets = null))
            .addOnSuccessListener {
                val cry = Cryptography(context)
                service.dataSets?.forEach { dataSet ->
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
                        rawData = Base64.getEncoder().encodeToString(md.digest(message))
                    }
                    val toUpload = cry.remoteEncryption(dataSet.copy(hashData = rawData))!!
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


    private suspend fun deleteRemoteCredential(dataSetId: Long,serviceName:String,dataSet:DataSet) {
        val updates = hashMapOf<String, Any>(
            "credentials" to dataSet.credentials!!
        )
        if (serviceName != null) {
            dataSet.hashData?.let {
                db.collection("users").document(user.uid)
                    .collection("services").document(serviceName)
                    .collection("dataSets").document(it)
                    .update(updates)

            }
        }

    }





    fun deleteFromRemote(dataSetId: Long, serviceName: String, dataSet: DataSet) {
        dataSet.hashData?.let {
            db.collection("users").document(user.uid)
                .collection("services").document(serviceName)
                .collection("dataSets").document(it)
                .delete()
                .addOnSuccessListener {

                }
        }

    }
}
