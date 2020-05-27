package com.example.finalprojectapp.dagger2.modules

import androidx.lifecycle.ViewModelProvider
import com.example.finalprojectapp.dagger2.DaggerViewModelFactory
import dagger.Binds
import dagger.Module

@Module
abstract class ViewModelFactoryModule {
    @Binds
    abstract fun bindViewModelFactory(viewModelFactory: DaggerViewModelFactory): ViewModelProvider.Factory
}