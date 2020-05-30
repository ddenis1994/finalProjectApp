package com.example.finalprojectapp.ui.di

import com.example.finalprojectapp.MainActivity
import com.example.finalprojectapp.autoFillService.di.AutoFIllServiceComponent
import com.example.finalprojectapp.ui.credentials.di.CredentialsFragmentComponent
import com.example.finalprojectapp.ui.dashboard.di.DashBoardFragmentComponent
import com.example.finalprojectapp.ui.notifications.di.NotificationFragmentComponent
import com.example.finalprojectapp.ui.settings.di.SettingsComponent
import dagger.Subcomponent

@Subcomponent(modules = [UIModule::class])
interface UIComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): UIComponent
    }

    fun settingsComponent(): SettingsComponent.Factory
    fun credentialViewModelComponent(): CredentialsFragmentComponent.Factory
    fun dashBoardFragmentComponent(): DashBoardFragmentComponent.Factory
    fun notificationFragmentComponent(): NotificationFragmentComponent.Factory

    fun inject(fragment: MainActivity)

}