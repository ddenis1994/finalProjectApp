package com.example.finalprojectapp.ui.di

import com.example.finalprojectapp.ui.auth.di.AuthComponent
import com.example.finalprojectapp.ui.credentials.di.CredentialsFragmentComponent
import com.example.finalprojectapp.ui.dashboard.di.DashBoardFragmentComponent
import com.example.finalprojectapp.ui.notifications.di.NotificationFragmentComponent
import com.example.finalprojectapp.ui.settings.di.SettingsComponent
import dagger.Module

@Module(subcomponents = [CredentialsFragmentComponent::class,SettingsComponent::class, DashBoardFragmentComponent::class,NotificationFragmentComponent::class,AuthComponent::class])
class UIModule