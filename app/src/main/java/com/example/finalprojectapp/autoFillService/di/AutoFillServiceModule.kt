package com.example.finalprojectapp.autoFillService.di


import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.finalprojectapp.autoFillService.adapters.DataSetAdapter
import com.example.finalprojectapp.utils.SingleEncryptedSharedPreferences
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import javax.inject.Inject
import javax.inject.Singleton

@Module
class AutoFillServiceModule  {




    @Inject
    lateinit var context: Context

    @Inject
    lateinit var dataSetAdapter: Lazy<DataSetAdapter>





    @Provides
    fun provideScope(): CoroutineScope {
        return CoroutineScope(Job() + Dispatchers.IO)
    }


    @Provides
    @Singleton
    fun provideSharedPreferences(context: Application): SharedPreferences {
        return SingleEncryptedSharedPreferences().getSharedPreference(context)
    }



//    @Provides
//    fun provideAutoFillServiceModule(
//        autoFillService: AutoFillService
//    ): AutoFillService {
//        return autoFillService
//    }


//    @Provides
//    fun providesDataSetAdapter(
//        localServiceDAO: ServiceRepository,
//        coroutineScope: CoroutineScope
//    ): DataSetAdapter {
//        return DataSetAdapter(localServiceDAO, packageName, coroutineScope)
//    }


}