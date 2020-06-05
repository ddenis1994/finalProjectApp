package com.example.finalprojectapp.workers

import android.content.Context
import android.content.SharedPreferences
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.finalprojectapp.credentialsDB.ServiceRepository
import com.example.finalprojectapp.crypto.LocalCryptography
import javax.inject.Inject

class ChangeEncryptionWorker(appContext: Context, workerParams: WorkerParameters)
    : CoroutineWorker(appContext, workerParams) {

    @Inject lateinit var settings:SharedPreferences
    @Inject lateinit var localCryptography: LocalCryptography
    @Inject lateinit var serviceRepository: ServiceRepository


    override suspend fun doWork(): Result {
        TODO("Not yet implemented")
    }
}