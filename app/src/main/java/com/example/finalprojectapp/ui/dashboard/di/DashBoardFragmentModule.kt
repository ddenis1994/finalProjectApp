package com.example.finalprojectapp.ui.dashboard.di

import androidx.lifecycle.ViewModel
import com.example.finalprojectapp.di.ViewModelKey
import com.example.finalprojectapp.ui.dashboard.DashboardViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class DashBoardFragmentModule {
    @Binds
    @IntoMap
    @ViewModelKey(DashboardViewModel::class)
    abstract fun bindViewModel(viewModel: DashboardViewModel): ViewModel
}