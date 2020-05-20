package com.example.finalprojectapp.ui.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.finalprojectapp.credentialsDB.NotificationRepository

class NotificationViewModelFactory(
    private val notificationRepository: NotificationRepository
):ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NotificationsViewModel(notificationRepository) as T
    }
}

