package com.example.finalprojectapp.credentialsDB

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.finalprojectapp.data.model.Notification

@Dao
interface NotificationDAO {

    @Query("Select * from notification")
    fun getAllNotification():LiveData<List<Notification>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: Notification):Long

    @Delete
    suspend fun deleteNotifications(notifications: List<Notification>)
    @Query("Delete from notification")
    suspend fun nukeAll()

    @Query("Select * from notification where hash like :notification")
    suspend fun checkForNotification(notification: String): Notification?
}