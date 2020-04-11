package com.example.finalprojectapp.workers

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DBWorkerDecryption(appContext: Context, workerParams: WorkerParameters)
    : Worker(appContext, workerParams) {
    override fun doWork(): Result  {
        val result=MutableLiveData<String>()
        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser!!

        db.collection("users").document(user.uid)
            .collection("services").get()
            .addOnSuccessListener {
                result.postValue("test")
            }

        result.observe(applicationContext as LifecycleOwner, Observer {


        })

        return Result.success()
    }


}