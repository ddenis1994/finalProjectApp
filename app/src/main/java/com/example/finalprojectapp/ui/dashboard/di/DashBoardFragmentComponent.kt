package com.example.finalprojectapp.ui.dashboard.di

import com.example.finalprojectapp.ui.dashboard.DashboardFragment
import dagger.Subcomponent

@Subcomponent(modules = [DashBoardFragmentModule::class])
interface DashBoardFragmentComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): DashBoardFragmentComponent
    }

    fun inject(fragment: DashboardFragment)
}