package com.example.finalprojectapp.credentialsDB

import android.service.autofill.SaveCallback
import com.example.finalprojectapp.crypto.RemoteCryptography
import com.example.finalprojectapp.data.model.DataSet
import com.example.finalprojectapp.data.model.Notification
import com.example.finalprojectapp.data.model.Service
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.*
import java.time.Instant
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class ServiceRepositoryRemote @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val coroutineScope: CoroutineScope,
    private val serviceRepositoryLocal: ServiceRepositoryLocal,
    private val remoteCryptography: RemoteCryptography
) {

    internal val user = FirebaseAuth.getInstance().currentUser
    private val db = FirebaseFirestore.getInstance()


    internal fun addDataToRemoteWithSaveCallBack(
        service: Service,
        callback: SaveCallback
    ) {
        val toUpload = remoteCryptography.encrypt(service)
        if (user != null) {
            db.collection("users").document(user.uid)
                .collection("services").document(service.name)
                .set(service.copy(dataSets = null))
                .addOnSuccessListener {
                    toUpload?.dataSets?.forEach { dataSet ->
                        dataSet.hashData?.let { dataSetHash ->
                            db.collection("users").document(user.uid)
                                .collection("services").document(service.name)
                                .collection("dataSets").document(dataSetHash)
                                .set(dataSet)
                                .addOnSuccessListener {
                                    callback.onSuccess()
                                    notificationRepository.insert(
                                        Notification(
                                            0, "Inserted Credentials", service.name,
                                            DateTimeFormatter.ISO_INSTANT.format(Instant.now())
                                        )
                                    )
                                }
                        }
                    }
                }
        }
    }


    internal suspend fun deleteRemoteCredential(
        dataSetId: Long,
        serviceName: String,
        dataSet: DataSet
    ) {
        val updates = hashMapOf<String, Any>(
            "credentials" to dataSet.credentials!!
        )
        dataSet.hashData?.let {
            user?.uid?.let { it1 ->
                db.collection("users").document(it1)
                    .collection("services").document(serviceName)
                    .collection("dataSets").document(it)
                    .update(updates)
            }

        }

    }


    fun deleteFromRemote(
        serviceName: String,
        dataSet: DataSet
    ) {
        if (user != null) {
            dataSet.hashData?.let {
                db.collection("users").document(user.uid)
                    .collection("services").document(serviceName)
                    .collection("dataSets").document(it)
                    .delete()
                    .addOnSuccessListener {
                        notificationRepository.insert(
                            Notification(
                                1,
                                "Delete DataSet",
                                serviceName,
                                DateTimeFormatter.ISO_INSTANT.format(Instant.now())
                            )
                        )
                    }
            }
        }

    }

    @ExperimentalCoroutinesApi
    private fun setOnUpdate() {

        if (user != null) {
            db.collection("users").document(user.uid)
                .collection("services").get()
                .addOnSuccessListener { result ->
                    coroutineScope.launch {
                        for (dc in result.documents) {
                            val service = dc.toObject<Service>() ?: continue
                            coroutineScope.launch {
                                val target = remoteCryptography.decryption(service)
                                if (target != null) {
                                    serviceRepositoryLocal.publicInsertService(target)
                                }
                            }

                        }

                    }
                }
        }
    }

    suspend fun sync() {
        if (user != null) {
            db.collection("users").document(user.uid)
                .collection("services").get()
                .addOnSuccessListener { result ->
                    for (dc in result.documents) {
                        val service = dc.toObject<Service>() ?: continue
                        db.collection("users").document(user.uid)
                            .collection("services").document(service.name)
                            .collection("dataSets").get().addOnSuccessListener { dataSets ->
                                coroutineScope.launch {
                                    val dataSetsList = mutableListOf<DataSet>()
                                    for (dataSet in dataSets.documents) {
                                        val dataSetD =
                                            dataSet.toObject<DataSet>() ?: continue
                                        dataSetsList.add(dataSetD)
                                    }
                                    val target =
                                        remoteCryptography.decryption(service.copy(dataSets = dataSetsList))
                                    this.launch {
                                        if (target != null) {
                                            serviceRepositoryLocal.publicInsertService(target)
                                        }
                                    }
                                }
                            }
                    }
                }
        }
    }


//    private fun listenerForService(name: String) {
//        db.collection("users").document(user.uid)
//            .collection("services").document(name)
//            .collection("dataSets")
//            .addSnapshotListener { querySnapshot, firebaseFireStoreException ->
//                if (firebaseFireStoreException != null)
//                    return@addSnapshotListener
//                for (dc in querySnapshot!!.documentChanges) {
//                    val dataSet=dc.document.toObject<DataSet>()
//                    when (dc.type) {
//                        DocumentChange.Type.REMOVED -> {
//                            lifecycleScope.launch {
//                                withContext(Dispatchers.IO){
//                                    val localDataSet= dataSet.hashData?.let {
//                                        localDB.applicationDAO().privateFindByHashData(it)
//                                    }
//                                    if (localDataSet != null) {
//                                        localDB.applicationDAO()
//                                            .privateDeleteDataSet(localDataSet)
//                                    }
//                                }
//
//                            }
//                        }
//                        else -> {
//                            lifecycleScope.launch {
//                                localDB.applicationDAO().publicInsertDataSet(dataSet,name)
//                                startLocalDecryption()
//                            }
//                        }
//                    }
//                }
//            }
//
//    }


}
