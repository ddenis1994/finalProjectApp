package com.example.finalprojectapp.ui.credentials.inner.di

import androidx.lifecycle.ViewModel
import com.example.finalprojectapp.di.ViewModelKey
import com.example.finalprojectapp.ui.credentials.inner.CredentialInnerViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class CredentialInnerFragmentModule {
    @Binds
    @IntoMap
    @ViewModelKey(CredentialInnerViewModel::class)
    abstract fun bindViewModel(viewmodel: CredentialInnerViewModel): ViewModel

}