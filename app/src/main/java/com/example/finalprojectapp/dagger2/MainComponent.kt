package com.example.finalprojectapp.dagger2

import android.content.Context
import androidx.fragment.app.Fragment
import com.example.finalprojectapp.MainActivity

import com.example.finalprojectapp.credentialsDB.ServiceRepository
import com.example.finalprojectapp.credentialsDB.ServiceRepositoryLocal
import com.example.finalprojectapp.crypto.LocalCryptography
import com.example.finalprojectapp.dagger2.modules.MainDaoModule
import com.example.finalprojectapp.ui.credentials.inner.CredentialInnerFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [MainDaoModule::class])
interface MainComponent {

    fun getLocalCryptography(): LocalCryptography
    fun getServiceRepositoryLocal(): ServiceRepositoryLocal
    fun getServiceRepository(): ServiceRepository
    fun inject(mainActivity: MainActivity)
    fun inject(credentialInnerFragment: CredentialInnerFragment)

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun context(context: Context):Builder

        fun build(): MainComponent


    }
}