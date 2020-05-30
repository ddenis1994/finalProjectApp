package com.example.finalprojectapp.di

import android.app.Application
import android.content.Context
import com.example.finalprojectapp.MainActivity
import com.example.finalprojectapp.autoFillService.di.AutoFIllServiceComponent

import com.example.finalprojectapp.di.modules.AppMainModule
import com.example.finalprojectapp.di.modules.DataBaseModule
import com.example.finalprojectapp.ui.credentials.di.CredentialsFragmentComponent
import com.example.finalprojectapp.ui.credentials.inner.di.CredentialInnerComponent
import com.example.finalprojectapp.ui.di.UIComponent
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import javax.inject.Singleton


@Component(modules = [AppMainModule::class, DataBaseModule::class,SubComponentsModule::class,ViewModelBuilderModule::class])
@Singleton
interface MainComponent {

    fun inject(mainActivity: MainActivity)

    fun autoFillServiceComponent(): AutoFIllServiceComponent.Factory
    fun credentialInnerViewModelComponent(): CredentialInnerComponent.Factory
    fun credentialViewModelComponent(): CredentialsFragmentComponent.Factory
    fun uiComponent(): UIComponent.Factory




    @Component.Builder
    interface Builder {

        @BindsInstance
        fun applicationContext(applicationContext: Application): Builder

        fun build(): MainComponent


    }
    val context:Context
}

@Module(subcomponents = [AutoFIllServiceComponent::class, UIComponent::class])
object SubComponentsModule