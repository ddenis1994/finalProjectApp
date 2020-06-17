package com.example.finalprojectapp

import android.app.Application
import com.example.finalprojectapp.di.DaggerMainComponent
import com.example.finalprojectapp.di.MainComponent

open class MainApplication: Application() {

    val appComponent: MainComponent by lazy {
        initializeComponent()
    }

    open fun initializeComponent(): MainComponent {
        // Creates an instance of AppComponent using its Factory constructor
        // We pass the applicationContext that will be used as Context in the graph
        return DaggerMainComponent.builder().applicationContext(this).build()
    }


    fun getComponent(): MainComponent {
        return appComponent
    }

}