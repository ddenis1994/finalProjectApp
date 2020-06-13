package com.example.finalprojectapp.autoFillService.di

import android.content.Context
import com.example.finalprojectapp.autoFillService.AutoFillService
import com.example.finalprojectapp.di.modules.DataBaseModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AutoFillServiceModule::class, DataBaseModule::class])
interface AutoFIllServiceComponent {



    @Component.Factory
    interface Factory{
        fun create(@BindsInstance context: Context):AutoFIllServiceComponent
    }


    fun inject(autoFillService: AutoFillService)


}