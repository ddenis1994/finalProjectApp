package com.example.finalprojectapp.workers

import android.content.Context
import android.content.SharedPreferences
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.finalprojectapp.credentialsDB.CredentialRepository
import com.example.finalprojectapp.credentialsDB.DataSetRepository
import com.example.finalprojectapp.credentialsDB.LocalDataBase
import com.example.finalprojectapp.credentialsDB.ServiceRepositoryLocal
import com.example.finalprojectapp.crypto.HashBuilder
import com.example.finalprojectapp.crypto.LocalCryptography
import com.example.finalprojectapp.utils.SingleEncryptedSharedPreferences

class ChangeEncryptionWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    private val settings: SharedPreferences =
        SingleEncryptedSharedPreferences().getSharedPreference(applicationContext)
    private val localCryptography: LocalCryptography = LocalCryptography(HashBuilder())
    private var serviceRepository: ServiceRepositoryLocal = ServiceRepositoryLocal(
        LocalDataBase.getDatabase(applicationContext).serviceDao(),
        DataSetRepository(
            CredentialRepository(
                LocalDataBase.getDatabase(applicationContext).credentialDAO(),
                localCryptography
            ),
            LocalDataBase.getDatabase(applicationContext).dataSetDAO(),
            localCryptography
        ),
        localCryptography
    )


    override suspend fun doWork(): Result {
        val newEncryptionType=inputData.getString("encryptionType")
        // TODO: 06/06/2020 need to add on credential dao get credentials with not newEncryptionType 
        // TODO: 06/06/2020 need to generate new key base on the encryption type
        // TODO: 06/06/2020 rencryprd the data with the new key
        // TODO: 06/06/2020 delete old key

        settings.edit().putBoolean("changeEncryptionType", false).apply()
        return Result.success()
    }
}