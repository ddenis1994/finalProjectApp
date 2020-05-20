package com.example.finalprojectapp.credentialsDB

import androidx.lifecycle.LiveData
import com.example.finalprojectapp.data.model.Notification

class NotificationRepository(
    private val notificationDAO: NotificationDAO
) {
    val allNotification = notificationDAO.getAllNotification()

    suspend fun insert(notification: Notification): Long {
        return notificationDAO.insertNotification(notification)
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