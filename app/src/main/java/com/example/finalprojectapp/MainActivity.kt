package com.example.finalprojectapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

import com.google.firebase.firestore.FirebaseFirestoreSettings




class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {


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
