package com.example.finalprojectapp.ui.credentials.di

import androidx.lifecycle.ViewModel
import com.example.finalprojectapp.di.ViewModelKey
import com.example.finalprojectapp.ui.credentials.CredentialsViewModel
import com.example.finalprojectapp.ui.credentials.inner.di.CredentialInnerComponent
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module(subcomponents = [CredentialInnerComponent::class])
abstract class CredentialsFragmentModule {
    @Binds
    @IntoMap
    @ViewModelKey(CredentialsViewModel::class)
    abstract fun bindViewModel(viewModel: CredentialsViewModel): ViewModel

}