package com.example.finalprojectapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.finalprojectapp.credentialsDB.ServiceRepositoryLocal
import com.example.finalprojectapp.dagger2.DaggerMainComponent
import com.example.finalprojectapp.dagger2.MainComponent
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import javax.inject.Inject


class MainActivity : AppCompatActivity() {


    @Inject
    lateinit var serviceRepositoryLocal: ServiceRepositoryLocal

    lateinit var applicationComponent: MainComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        applicationComponent = (application as MainApplication).getComponent()
        applicationComponent.inject(this)
        serviceRepositoryLocal.getAllData()

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


        setContentView(R.layout.activity_main)
    }

}



