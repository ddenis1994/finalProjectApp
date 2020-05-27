package com.example.finalprojectapp.dagger2.modules

import android.content.Context
import com.example.finalprojectapp.credentialsDB.CredentialDAO
import com.example.finalprojectapp.credentialsDB.DataSetDAO
import com.example.finalprojectapp.credentialsDB.LocalDataBase
import com.example.finalprojectapp.credentialsDB.ServiceDAO
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class MainDaoModule {

    @Singleton
    @Provides
    fun credentialsDao(context: Context): CredentialDAO {
        return LocalDataBase.getDatabase(context).credentialDAO()
    }

    @Singleton
    @Provides
    fun dataSetDao(context: Context): DataSetDAO {
        return LocalDataBase.getDatabase(context).dataSetDAO()
    }

    @Singleton
    @Provides
    fun serviceDao(context: Context): ServiceDAO {
        return LocalDataBase.getDatabase(context).serviceDao()
    }


}