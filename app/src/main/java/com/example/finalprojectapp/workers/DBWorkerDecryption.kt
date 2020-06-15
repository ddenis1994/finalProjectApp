package com.example.finalprojectapp.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.finalprojectapp.credentialsDB.LocalDataBase
import com.example.finalprojectapp.crypto.LocalCryptography
import com.example.finalprojectapp.crypto.RemoteCryptography

class DBWorkerDecryption(appContext: Context, workerParams: WorkerParameters)
    : CoroutineWorker(appContext, workerParams) {
    lateinit var localCryptography:LocalCryptography
    override suspend  fun doWork(): Result  {
        val localDB= LocalDataBase.getDatabase(applicationContext)
        val test = localDB.credentialDAO().getCredentialsWithSalt()

        val remoteCryptography= RemoteCryptography(applicationContext)

        test.forEach {
            val localTemp=localCryptography.encrypt(remoteCryptography.decryption(it))
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