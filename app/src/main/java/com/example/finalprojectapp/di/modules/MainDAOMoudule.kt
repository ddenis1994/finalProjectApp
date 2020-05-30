package com.example.finalprojectapp.di.modules

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Inject

@Module
class MainDaoModule {

    @Provides
    @Inject
    fun context(context: Application): Context {
        return context.applicationContext
    }





}