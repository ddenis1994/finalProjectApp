package com.example.finalprojectapp.credentialsDB

import com.example.finalprojectapp.data.model.Notification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NotificationRepository(
    private val notificationDAO: NotificationDAO
) {
    val allNotification = notificationDAO.getAllNotification()

    suspend fun insert(notification: Notification): Long {
        remoteInsert(notification)
        return withContext(Dispatchers.IO) {
            return@withContext localInsert(notification)
        }
    }

    private suspend fun localInsert(notification: Notification): Long {
        return notificationDAO.insertNotification(notification)
    }

    private fun remoteInsert(notification: Notification) {

    }



    suspend fun delete(notifications: List<Notification>) {
        return notificationDAO.deleteNotifications(notifications)
    }


    companion object {
        // For Singleton instantiation
        @Volatile private var instance: NotificationRepository? = null
        fun getInstance(notificationDAO: NotificationDAO) =
            instance ?: synchronized(this) {
                instance
                    ?: NotificationRepository(
                        notificationDAO
                    )
                        .also { instance = it }
            }
    }


}