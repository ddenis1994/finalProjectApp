package com.example.finalprojectapp.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.finalprojectapp.credentialsDB.LocalDataBase
import com.example.finalprojectapp.crypto.LocalCryptography
import com.example.finalprojectapp.crypto.RemoteCryptography
import javax.inject.Inject

class DBWorkerDecryption(appContext: Context, workerParams: WorkerParameters)
    : CoroutineWorker(appContext, workerParams) {
    @Inject lateinit var localCryptography:LocalCryptography
    override suspend  fun doWork(): Result  {
        val localDB= LocalDataBase.getDatabase(applicationContext)
        val test = localDB.credentialDAO().getAllEncryptedCredentials()

        val remoteCryptography= RemoteCryptography(applicationContext)

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