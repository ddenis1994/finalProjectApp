package com.example.finalprojectapp

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.finalprojectapp.crypto.Cryptography
import com.example.finalprojectapp.data.model.Credentials
import com.example.finalprojectapp.data.model.DataSet
import com.google.firebase.firestore.FirebaseFirestore

import com.google.firebase.firestore.FirebaseFirestoreSettings




class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
/*
        //TODO remove the firestore setting in the production
        val settings = FirebaseFirestoreSettings.Builder()
            .setHost("10.0.2.2:8080")
            .setSslEnabled(false)
            .setPersistenceEnabled(true)
            .build()

        val firestore = FirebaseFirestore.getInstance()
        firestore.firestoreSettings = settings
        //end of fire base settings


 */
        //val cry=Cryptography(this)
        //val o=DataSet(credentials = listOf(Credentials().copy(data = "test")))
        //val h=cry.remoteEncryption(o)

       // val i=cry.remoteDecryptSingle(h!!.credentials!![0])

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
