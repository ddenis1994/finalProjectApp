package com.example.finalprojectapp.dagger2

import android.content.Context
import com.example.finalprojectapp.MainActivity

import com.example.finalprojectapp.credentialsDB.ServiceRepository
import com.example.finalprojectapp.credentialsDB.ServiceRepositoryLocal
import com.example.finalprojectapp.crypto.LocalCryptography
import com.example.finalprojectapp.dagger2.modules.MainDaoModule
import dagger.BindsInstance
import dagger.Component

@Component(modules = [MainDaoModule::class])
interface MainComponent {

    fun getLocalCryptography(): LocalCryptography
    fun getServiceRepositoryLocal(): ServiceRepositoryLocal
    fun getServiceRepository(): ServiceRepository
    fun inject(mainActivity: MainActivity)

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun context(context: Context):Builder

        fun build(): MainComponent


    }
}