package com.example.finalprojectapp

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.finalprojectapp.credentialsDB.*
import com.example.finalprojectapp.crypto.LocalCryptography
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Inject


class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var serviceRepositoryLocal: ServiceRepositoryLocal

    lateinit var applicationComponent:ApplicationComponent

    override fun onCreate(savedInstanceState: Bundle?) {


        applicationComponent = DaggerApplicationComponent.builder().context(this).build()
        //applicationComponent.inject(this)
        //serviceRepositoryLocal.getAllData()

/*
        //TODO remove the firestore setting in the production
        val settings = FirebaseFirestoreSettings.Builder()
            .setHost("10.0.2.2:8080")
            .setSslEnabled(false)
            .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
            .setPersistenceEnabled(true)
            .build()

        val firestore = FirebaseFirestore.getInstance()
        firestore.firestoreSettings = settings
        //end of fire base settings


 */
        val settings = FirebaseFirestoreSettings.Builder()
            .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
            .setPersistenceEnabled(true)
            .build()
        val firestore = FirebaseFirestore.getInstance()
        firestore.firestoreSettings = settings







        supportActionBar?.hide()
        //actionBar?.hide()


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

}


@Component(modules = [MainActivityModule::class])
interface ApplicationComponent {

    fun getLocalCryptography(): LocalCryptography
    fun getServiceRepositoryLocal(): ServiceRepositoryLocal
    fun getServiceRepository(): ServiceRepository
    fun inject(mainActivity: MainActivity)

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun context(context: Context):Builder

        fun build(): ApplicationComponent


    }
}


@Module
class MainActivityModule {

    @Provides
    fun credentialsDao(context: Context): CredentialDAO {
        return LocalDataBase.getDatabase(context).credentialDAO()
    }

    @Provides
    fun dataSetDao(context: Context): DataSetDAO {
        return LocalDataBase.getDatabase(context).dataSetDAO()
    }

    @Provides
    fun serviceDao(context: Context): ServiceDAO {
        return LocalDataBase.getDatabase(context).serviceDao()
    }


}


