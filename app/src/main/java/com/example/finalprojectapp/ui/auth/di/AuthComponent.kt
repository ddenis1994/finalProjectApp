package com.example.finalprojectapp.ui.auth.di

import com.example.finalprojectapp.ui.auth.AppAuthActivity
import com.example.finalprojectapp.ui.auth.ServiceAuthActivity
import com.example.finalprojectapp.ui.credentials.CredentialsFragment
import com.example.finalprojectapp.ui.credentials.di.CredentialsFragmentModule
import dagger.Subcomponent

@Subcomponent(modules = [AuthModule::class])
interface AuthComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): AuthComponent
    }
    fun inject(authActivity: AppAuthActivity)
    fun inject(serviceAuthActivity: ServiceAuthActivity)
}


