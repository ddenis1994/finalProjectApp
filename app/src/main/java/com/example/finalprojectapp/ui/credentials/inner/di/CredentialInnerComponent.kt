package com.example.finalprojectapp.ui.credentials.inner.di

import com.example.finalprojectapp.ui.credentials.inner.CredentialInnerFragment
import dagger.Subcomponent

@Subcomponent(modules = [CredentialInnerFragmentModule::class])
interface CredentialInnerComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): CredentialInnerComponent
    }

    fun inject(fragment: CredentialInnerFragment)

}