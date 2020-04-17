package com.example.finalprojectapp.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.finalprojectapp.crypto.Cryptography
import com.example.finalprojectapp.localDB.PasswordRoomDatabase

class DBWorkerDecryption(appContext: Context, workerParams: WorkerParameters)
    : CoroutineWorker(appContext, workerParams) {
    override suspend  fun doWork(): Result  {
        val localDB= PasswordRoomDatabase.getDatabase(applicationContext)
        val test = localDB.localCredentialsDAO().getAllEncryptedCredentials()
        val cryptography=Cryptography(applicationContext)

        test.forEach {
            val localTemp=cryptography.localEncryptSingle(cryptography.remoteDecryptSingle(it))
            localTemp?.let { it1 ->
                localDB.localCredentialsDAO().updateCredentials(it1)
            }

        }

        with (applicationContext.getSharedPreferences("mainPreferences",Context.MODE_PRIVATE).edit()) {
            putBoolean("encrypted", false)
            commit()
        }
        return Result.success()
    }
}