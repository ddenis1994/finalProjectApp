package com.example.finalprojectapp.crypto

import com.example.finalprojectapp.data.model.Credentials
import com.example.finalprojectapp.data.model.DataSet
import com.example.finalprojectapp.data.model.Service
import javax.inject.Inject

open class InnerCryptography @Inject constructor() {
    private val hashBuilder=HashBuilder()


    private fun encryptDataSet(dataSet: DataSet?): DataSet? {
        var newDataSet = dataSet ?: return null
        if (newDataSet.hashData.isNullOrEmpty())
            newDataSet = hashBuilder.makeHash(newDataSet) as DataSet

        if (newDataSet.credentials == null)
            return dataSet
        val newEncryptedCredentials = mutableListOf<Credentials>()
        newDataSet.credentials?.forEach {
            encrypt(it)?.let { it1 -> newEncryptedCredentials.add(it1) }
        }
        return newDataSet.copy(credentials = newEncryptedCredentials)
    }

    private fun encryptService(target: Service?): Service? {
        var new = target ?: return null

        if (new.hash.isEmpty())
            new = hashBuilder.makeHash(new) as Service

        val newEncryptedList = mutableListOf<DataSet>()
        new.dataSets?.forEach {
            encrypt(it)?.let { it1 -> newEncryptedList.add(it1) }
        }
        return new.copy(dataSets = newEncryptedList)
    }



    private fun decryptDataSet(dataSet: DataSet?): DataSet? {
        val newDataSet: DataSet = dataSet?.copy() ?: return null
        val newCredentials = mutableListOf<Credentials>()
        newDataSet.credentials?.forEach { cre ->
            decryption(cre)?.let { newCredentials.add(it) }
        }

        return newDataSet.copy(credentials = newCredentials)
    }

    private fun decryptService(target: Service?): Service? {
        val new = target?.copy() ?: return null

        val newDecryptList = mutableListOf<DataSet>()

        new.dataSets?.forEach {
            decryption(it)?.let { it1 -> newDecryptList.add(it1) }
        }

        return new.copy(dataSets = newDecryptList)
    }


    @Suppress("UNCHECKED_CAST")
    fun <T> encrypt(target: T): T? {
        return when (target) {
            is DataSet -> encryptDataSet(target) as T
            is Service -> encryptService(target) as T
            else -> null
        }
    }


    @Suppress("UNCHECKED_CAST")
    fun <T> decryption(target: T): T? {
        return when (target) {
            is DataSet -> decryptDataSet(target) as T
            is Service -> decryptService(target) as T
            else -> null
        }
    }


}