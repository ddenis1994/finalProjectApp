package com.example.finalprojectapp.autoFillService.di

import com.example.finalprojectapp.autoFillService.AutoFillService
import dagger.Subcomponent
import javax.inject.Singleton

@Subcomponent(modules = [AutoFillServiceModule::class])
interface AutoFIllServiceComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): AutoFIllServiceComponent
    }

    fun inject(autoFillService: AutoFillService)



}