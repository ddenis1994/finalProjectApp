package com.example.finalprojectapp.credentialsDB

import com.example.finalprojectapp.data.model.Notification
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import javax.inject.Inject

class NotificationRepository @Inject constructor(
    private val notificationDAO: NotificationDAO,
    private val coroutineScope:CoroutineScope
) {
    private val db=Firebase.firestore
    private val user = FirebaseAuth.getInstance().currentUser


    val allNotification = notificationDAO.getAllNotification()

    fun insert(notification: Notification) {
        remoteInsert(notification)
    }

    private suspend fun localInsert(notification: Notification): Long {
        return notificationDAO.insertNotification(notification)
    }

    private fun remoteInsert(notification: Notification) {
        user?.uid?.let {
            db.collection("users").document(it)
                .collection("notifications").add(notification.copy()).addOnSuccessListener {
                    coroutineScope.launch {
                        withContext(Dispatchers.IO){
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
        // For Singleton instantiation
        @Volatile private var instance: NotificationRepository? = null
        fun getInstance(notificationDAO: NotificationDAO,coroutineScope:CoroutineScope) =
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