package com.example.finalprojectapp.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.finalprojectapp.DaggerApplicationComponent
import com.example.finalprojectapp.credentialsDB.LocalDataBase
import com.example.finalprojectapp.crypto.LocalCryptography
import com.example.finalprojectapp.crypto.RemoteCryptography

class DBWorkerDecryption(appContext: Context, workerParams: WorkerParameters)
    : CoroutineWorker(appContext, workerParams) {
    override suspend  fun doWork(): Result  {
        val localDB= LocalDataBase.getDatabase(applicationContext)
        val test = localDB.credentialDAO().getAllEncryptedCredentials()

        val remoteCryptography= RemoteCryptography(applicationContext)
        val applicationComponent= DaggerApplicationComponent.create()
        val localCryptography:LocalCryptography=applicationComponent.getLocalLocalCryptography()

        test.forEach {
            val localTemp=localCryptography.localEncrypt(remoteCryptography.remoteDecryption(it))
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