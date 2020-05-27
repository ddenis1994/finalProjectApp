package com.example.finalprojectapp

import android.app.Application
import com.example.finalprojectapp.dagger2.DaggerMainComponent
import com.example.finalprojectapp.dagger2.MainComponent

class MainApplication: Application() {

    private lateinit var applicationComponent: MainComponent
    override fun onCreate() {
        super.onCreate()
        applicationComponent=DaggerMainComponent.builder().context(this).build()
        
    }
    fun getComponent():MainComponent {
        return applicationComponent
    }

}