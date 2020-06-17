package com.example.finalprojectapp.credentialsDB

import com.example.finalprojectapp.crypto.RemoteCryptography
import com.example.finalprojectapp.data.model.DataSet
import com.example.finalprojectapp.data.model.Notification
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import java.time.Instant
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class DataSetRepositoryRemote @Inject constructor(
    private val db: FirebaseFirestore,
    private val user: FirebaseUser?,
    private val remoteCryptography: RemoteCryptography,
    private val notificationRepository: NotificationRepository
) {

    fun insertDataSet(serviceName: String,vararg dataSets: DataSet ) {
        if (user == null) return
        dataSets.forEach lit@{ dataSet ->
            val target = if (dataSet.hashData.isEmpty()) remoteCryptography.encrypt(dataSet)?: return@lit
            else dataSet
            db.collection("users").document(user.uid)
                .collection("services").document(serviceName)
                .collection("dataSets").document(target.hashData)
                .set(target)
                .addOnSuccessListener {
                    notificationRepository.insert(
                        Notification(
                            0, "Inserted Credentials", serviceName,
                            DateTimeFormatter.ISO_INSTANT.format(Instant.now())
                        )
                    )
                }
        }
    }
}