package com.example.finalprojectapp.ui.notifications.di

import androidx.lifecycle.ViewModel
import com.example.finalprojectapp.di.ViewModelKey
import com.example.finalprojectapp.ui.credentials.CredentialsViewModel
import com.example.finalprojectapp.ui.notifications.NotificationsViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class NotificationFragmentModule {

    @Binds
    @IntoMap
    @ViewModelKey(NotificationsViewModel::class)
    abstract fun bindViewModel(viewModel: NotificationsViewModel): ViewModel
}