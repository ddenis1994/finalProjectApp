package com.example.finalprojectapp.ui.credentials.di

import com.example.finalprojectapp.ui.credentials.CredentialsFragment
import dagger.Subcomponent

@Subcomponent(modules = [CredentialsFragmentModule::class])
interface CredentialsFragmentComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): CredentialsFragmentComponent
    }

    fun inject(fragment: CredentialsFragment)
}