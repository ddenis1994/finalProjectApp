package com.example.finalprojectapp.di

import android.app.Application
import android.content.Context
import android.service.autofill.AutofillService
import com.example.finalprojectapp.MainActivity
import com.example.finalprojectapp.autoFillService.di.AutoFIllServiceComponent
import com.example.finalprojectapp.autoFillService.di.AutoFillServiceModule

import com.example.finalprojectapp.di.modules.MainDaoModule
import com.example.finalprojectapp.ui.credentials.inner.CredentialInnerFragment
import com.example.finalprojectapp.ui.credentials.inner.di.CredentialInnerComponent
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import javax.inject.Singleton


@Component(modules = [MainDaoModule::class, DataBaseModule::class,SubComponentsModule::class])
@Singleton
interface MainComponent {

    fun inject(mainActivity: MainActivity)
    fun inject(credentialInnerFragment: CredentialInnerFragment)

    fun autoFillServiceComponent(): AutoFIllServiceComponent.Factory




    @Component.Builder
    interface Builder {

        @BindsInstance
        fun applicationContext(applicationContext: Application): Builder

        fun build(): MainComponent


    }
    val context:Context
}

@Module(subcomponents = [CredentialInnerComponent::class,AutoFIllServiceComponent::class])
object SubComponentsModule