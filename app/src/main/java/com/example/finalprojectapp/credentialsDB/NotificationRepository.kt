package com.example.finalprojectapp.credentialsDB

import com.example.finalprojectapp.crypto.HashBuilder
import com.example.finalprojectapp.data.model.Notification
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import javax.inject.Inject

class NotificationRepository @Inject constructor(
    private val notificationDAO: NotificationDAO,
    private val coroutineScope: CoroutineScope
) {
    private val db = Firebase.firestore
    private val user = FirebaseAuth.getInstance().currentUser

    private val hashBuilder=HashBuilder()

    val allNotification = notificationDAO.getAllNotification()

    fun insert(notification: Notification) {
        val target=hashBuilder.makeHash(notification)
        if (target != null) {
            remoteInsert(target)
        }
    }

    @ExperimentalCoroutinesApi
    fun syncNotification() {
        if (user != null) {
            db.collection("users").document(user.uid)
                .collection("notifications").get().addOnSuccessListener {
                        result ->
                    coroutineScope.launch {
                        for (dc in result.documents) {
                            val notification = dc.toObject<Notification>() ?: continue
                            val check = coroutineScope.async {
                                checkForNotification(notification)
                            }
                            check.await()
                            if (check.getCompleted()){
                                localInsert(notification)
                            }
                        }
                    }
                }
        }
    }

    private suspend fun checkForNotification(notification: Notification): Boolean {
        return notificationDAO.checkForNotification(notification.hash) == null
    }

    private suspend fun localInsert(notification: Notification): Long {
        return notificationDAO.insertNotification(notification)
    }

    private fun remoteInsert(notification: Notification) {
        user?.uid?.let {
            db.collection("users").document(it)
                .collection("notifications").add(notification.copy()).addOnSuccessListener {
                    coroutineScope.launch {
                        withContext(Dispatchers.IO) {
                            localInsert(notification)
                        }
                    }

                }
        }
    }

    suspend fun nukeAllNotification(): Unit {
        notificationDAO.nukeAll()
    }


    suspend fun delete(notifications: List<Notification>) {
        return notificationDAO.deleteNotifications(notifications)
    }


    companion object {
        @Volatile
        private var instance: NotificationRepository? = null
        fun getInstance(notificationDAO: NotificationDAO, coroutineScope: CoroutineScope) =
            instance ?: synchronized(this) {
                instance
                    ?: NotificationRepository(
                        notificationDAO,
                        coroutineScope
                    )
                        .also { instance = it }
            }
    }


}