package com.example.finalprojectapp.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.finalprojectapp.crypto.CredentialEncrypt
import com.example.finalprojectapp.localDB.PasswordRoomDatabase

class DBWorkerDecryption(appContext: Context, workerParams: WorkerParameters)
    : CoroutineWorker(appContext, workerParams) {
    override suspend  fun doWork(): Result  {
        val localDB= PasswordRoomDatabase.getDatabase(applicationContext)
        val decryption =CredentialEncrypt("password")
        val test = localDB.localCredentialsDAO().getAllEncryptedCredentials()
        test.forEach {
            localDB.localCredentialsDAO().updateCredentials(decryption.decrypt(it))
        }
        with (applicationContext.getSharedPreferences("mainPreferences",Context.MODE_PRIVATE).edit()) {
            putBoolean("encrypted", false)
            commit()
        }
        return Result.success()
    }
}