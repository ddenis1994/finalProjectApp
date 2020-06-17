package com.example.finalprojectapp.autoFillService.di


import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.finalprojectapp.autoFillService.adapters.DataSetAdapter
import com.example.finalprojectapp.utils.SingleEncryptedSharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject
import javax.inject.Singleton

@Module
class AutoFillServiceModule  {

    @Inject
    lateinit var context: Context

    @Inject
    lateinit var dataSetAdapter: Lazy<DataSetAdapter>


    @Provides
    fun provideScope(): CoroutineScope {
        return CoroutineScope(Dispatchers.IO+ SupervisorJob())
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(context:Context): SharedPreferences {
        return SingleEncryptedSharedPreferences().getSharedPreference(context.applicationContext)
    }

    @Provides
    @Singleton
    fun provideRemoteDataBase(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideRemoteUser(): FirebaseUser? {
        return FirebaseAuth.getInstance().currentUser
    }

}