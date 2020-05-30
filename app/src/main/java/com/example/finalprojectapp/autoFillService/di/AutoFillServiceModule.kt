package com.example.finalprojectapp.autoFillService.di


import com.example.finalprojectapp.autoFillService.AutoFillService
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import javax.inject.Inject
import javax.inject.Singleton

@Module
 class AutoFillServiceModule  {

    private lateinit var packageName: String

    @Provides
    @Singleton
    @Inject
    fun provideAutoFillServiceModule(autoFillService:AutoFillService): AutoFillService {
        return autoFillService
    }

    fun setPackageNameParsing(packageName:String){
        this.packageName=packageName
    }

    @Provides
    @Singleton
    @Inject
    fun providePackageNameParsing(): String {
        return this.packageName
    }



}