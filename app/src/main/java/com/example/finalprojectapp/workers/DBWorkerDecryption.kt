package com.example.finalprojectapp.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.finalprojectapp.crypto.CredentialEncrypt
import com.example.finalprojectapp.localDB.PasswordRoomDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DBWorkerDecryption(appContext: Context, workerParams: WorkerParameters)
    : CoroutineWorker(appContext, workerParams) {
    override suspend  fun doWork(): Result  {
        val localDB= PasswordRoomDatabase.getDatabase(applicationContext)
        val decryption: CredentialEncrypt=CredentialEncrypt("password")
        val test = localDB.localCredentialsDAO().getAllEncryptedCredentials()
        test.forEach {
            localDB.localCredentialsDAO().insert(decryption.decrypt(it))
        }

        return Result.success()
    }


}