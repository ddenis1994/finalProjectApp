package com.example.finalprojectapp.di.modules

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.finalprojectapp.utils.SingleEncryptedSharedPreferences
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import javax.inject.Inject
import javax.inject.Singleton

@Module
class AppMainModule {

    @Provides
    fun context(context: Application): Context {
        return context.applicationContext
    }

    @Provides
    @Singleton
    fun provideScope(): CoroutineScope {
        return CoroutineScope(Job() + Dispatchers.IO)
    }



    @Provides
    @Singleton
    fun provideSharedPreferences(context: Application): SharedPreferences {
        return SingleEncryptedSharedPreferences().getSharedPreference(context)
    }





}