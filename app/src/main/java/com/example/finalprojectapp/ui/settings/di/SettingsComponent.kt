package com.example.finalprojectapp.ui.settings.di

import com.example.finalprojectapp.ui.settings.SettingsFragment
import dagger.Subcomponent
import javax.inject.Singleton


@Subcomponent(modules = [SettingsModule::class])
interface SettingsComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): SettingsComponent
    }
    fun inject(settingFragment: SettingsFragment)
}