package com.example.finalprojectapp.credentialsDB

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
    private val remoteCryptography: RemoteCryptography,
    private val dataSetRepositoryRemote: DataSetRepositoryRemote
) {

    internal val user = FirebaseAuth.getInstance().currentUser
    private val db = FirebaseFirestore.getInstance()


    internal fun addDataToRemoteWithSaveCallBack(
        service: Service
    ) {
        val toUpload = remoteCryptography.encrypt(service)
        if (user != null) {
            db.collection("users").document(user.uid)
                .collection("services").document(service.name)
                .set(service.copy(dataSets = null))
                .addOnSuccessListener {
                    toUpload?.dataSets?.toTypedArray()
                        ?.let { it1 -> dataSetRepositoryRemote.insertDataSet(toUpload.name,*it1 ) }
                }
        }
    }

    internal fun deleteRemoteDataSet(
        service: Service,
        hashData: String
    ) {
        user?.uid?.let { uid ->
            db.collection("users").document(uid)
                .collection("services").document(service.name)
                .collection("dataSets").document(hashData)
                .delete().addOnSuccessListener {
                    updateAfterDeleteDataSet(service)
                }
        }


    }

    private fun updateAfterDeleteDataSet(service: Service) {
        val newHashService = service.hash
        val update = mapOf(
            "hash" to newHashService
        )
        user?.uid?.let { it1 ->
            db.collection("users").document(it1)
                .collection("services").document(service.name)
                .update(update).addOnSuccessListener {
                    notificationRepository.insert(
                        Notification(
                            1,
                            "Delete DataSet",
                            service.name,
                            DateTimeFormatter.ISO_INSTANT.format(Instant.now())
                        )
                    )
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
                                    serviceRepositoryLocal.publicInsertService(target, null)
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
                                            serviceRepositoryLocal.publicInsertService(
                                                target,
                                                null
                                            )
                                        }
                                    }
                                }
                            }
                    }
                }
        }
    }

    fun deleteRemoteCredential(service: Service, dataSet: DataSet) {

        val update = mapOf(
            "credentials" to dataSet.credentials
        )
        user?.uid?.let { it1 ->
            db.collection("users").document(it1)
                .collection("services").document(service.name)
                .collection("dataSets").document(dataSet.hashData)
                .update(update)
                .addOnSuccessListener {
                    updateAfterDeleteDataSet(service)
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
