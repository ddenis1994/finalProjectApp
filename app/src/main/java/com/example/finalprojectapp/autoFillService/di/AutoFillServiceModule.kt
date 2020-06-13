package com.example.finalprojectapp.autoFillService.di


import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.finalprojectapp.autoFillService.adapters.DataSetAdapter
import com.example.finalprojectapp.utils.SingleEncryptedSharedPreferences
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import javax.inject.Inject
import javax.inject.Singleton

@Module
class AutoFillServiceModule  {

    @Inject
    lateinit var context: Context

    @Inject
    lateinit var dataSetAdapter: Lazy<DataSetAdapter>


    @Provides
    @Singleton
    fun provideScope(): CoroutineScope {
        return CoroutineScope(Job() + Dispatchers.IO)
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(context:Context): SharedPreferences {
        return SingleEncryptedSharedPreferences().getSharedPreference(context.applicationContext)
    }

}