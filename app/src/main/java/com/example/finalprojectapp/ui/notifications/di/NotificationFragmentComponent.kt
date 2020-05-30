package com.example.finalprojectapp.ui.notifications.di

import com.example.finalprojectapp.ui.notifications.NotificationsFragment
import dagger.Subcomponent

@Subcomponent(modules = [NotificationFragmentModule::class])
interface NotificationFragmentComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): NotificationFragmentComponent
    }

    fun inject(fragment: NotificationsFragment)
}