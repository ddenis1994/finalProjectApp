package com.example.finalprojectapp.di

import android.app.Application
import android.content.Context
import android.service.autofill.AutofillService
import com.example.finalprojectapp.MainActivity

import com.example.finalprojectapp.di.modules.MainDaoModule
import com.example.finalprojectapp.ui.credentials.inner.CredentialInnerFragment
import com.example.finalprojectapp.ui.credentials.inner.di.CredentialInnerComponent
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import javax.inject.Singleton

@Singleton
@Component(modules = [MainDaoModule::class, DataBaseModule::class,SubcomponentsModule::class])
interface MainComponent {

    fun inject(mainActivity: MainActivity)
    fun inject(credentialInnerFragment: CredentialInnerFragment)
    fun inject(credentialInnerFragment: AutofillService)




    @Component.Builder
    interface Builder {

        @BindsInstance
        fun applicationContext(applicationContext: Application): Builder

        fun build(): MainComponent


    }
    val context:Context
}

@Module(subcomponents = [CredentialInnerComponent::class])
object SubcomponentsModule