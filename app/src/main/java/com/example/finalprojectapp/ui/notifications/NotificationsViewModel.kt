package com.example.finalprojectapp.ui.notifications

import androidx.lifecycle.ViewModel
import com.example.finalprojectapp.credentialsDB.NotificationRepository

class NotificationsViewModel(
    notificationRepository: NotificationRepository
) : ViewModel() {

    private val _data = notificationRepository.allNotification
    val data=_data





}