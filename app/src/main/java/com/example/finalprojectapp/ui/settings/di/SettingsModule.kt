package com.example.finalprojectapp.ui.settings.di

import android.content.Context
import android.view.autofill.AutofillManager
import androidx.core.content.ContextCompat
import dagger.Module
import dagger.Provides

@Module
class SettingsModule {


    @Provides
    fun provideAutoFillManager(context:Context): AutofillManager {
        return ContextCompat.getSystemService(context, AutofillManager::class.java)!!
    }

}