package com.example.finalprojectapp.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.finalprojectapp.credentialsDB.NotificationRepository
import com.example.finalprojectapp.data.model.Notification

class NotificationsViewModel(
    notificationRepository: NotificationRepository
) : ViewModel() {

    private val _data = notificationRepository.allNotification
    val data=_data





}