package com.example.finalprojectapp.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.finalprojectapp.credentialsDB.LocalDataBase
import com.example.finalprojectapp.crypto.Cryptography

class DBWorkerDecryption(appContext: Context, workerParams: WorkerParameters)
    : CoroutineWorker(appContext, workerParams) {
    override suspend  fun doWork(): Result  {
        val localDB= LocalDataBase.getDatabase(applicationContext)
        val test = localDB.credentialDAO().getAllEncryptedCredentials()
        val cryptography=Cryptography(applicationContext)

        test.forEach {
            val localTemp=cryptography.localEncryptSingle(cryptography.remoteDecryptSingle(it))
            localTemp?.let { it1 ->
                localDB.credentialDAO().updateCredentials(it1)
            }
        }


        with (applicationContext.getSharedPreferences("mainPreferences",Context.MODE_PRIVATE).edit()) {
            putBoolean("encrypted", false)
            commit()
        }
        return Result.success()
    }
}