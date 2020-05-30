package com.example.finalprojectapp.di.modules

import android.content.Context
import com.example.finalprojectapp.credentialsDB.*
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module

class DataBaseModule {


    @Singleton
    @Provides
    fun provideDataBase(context: Context): LocalDataBase{
        return LocalDataBase.getDatabase(context)
    }


    @Provides
    fun credentialsDao(dataBase: LocalDataBase): CredentialDAO {
        return dataBase.credentialDAO()
    }


    @Provides
    fun dataSetDao(dataBase: LocalDataBase): DataSetDAO {
        return dataBase.dataSetDAO()
    }


    @Provides
    fun serviceDao(dataBase: LocalDataBase): ServiceDAO {
        return dataBase.serviceDao()
    }

    @Provides
    fun providerNotificationDAO(dataBase: LocalDataBase): NotificationDAO {
        return dataBase.notificationDao()
    }

}