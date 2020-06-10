package com.example.finalprojectapp.ui.auth.di

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.core.content.ContextCompat
import dagger.Module
import dagger.Provides
import java.util.concurrent.Executor
import javax.inject.Singleton

@Module
class AuthModule {
    //    @Binds
//    @IntoMap
//    @ViewModelKey(CredentialsViewModel::class)
//    abstract fun bindViewModel(viewModel: CredentialsViewModel): ViewModel
    @Provides
    fun provideBiometricManager(context:Context): BiometricManager {
        return BiometricManager.from(context)
    }

    @Provides
    fun provideExacter(context:Context): Executor {
        return ContextCompat.getMainExecutor(context)
    }

}


