package com.example.finalprojectapp.ui.di

import com.example.finalprojectapp.ui.credentials.di.CredentialsFragmentComponent
import com.example.finalprojectapp.ui.credentials.inner.di.CredentialInnerComponent
import com.example.finalprojectapp.ui.settings.di.SettingsComponent
import dagger.Module

@Module(subcomponents = [CredentialsFragmentComponent::class,SettingsComponent::class])
class UIModule