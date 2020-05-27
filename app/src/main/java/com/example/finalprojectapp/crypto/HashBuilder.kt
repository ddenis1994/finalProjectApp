package com.example.finalprojectapp.crypto

import com.example.finalprojectapp.data.model.Credentials
import com.example.finalprojectapp.data.model.DataSet
import com.example.finalprojectapp.data.model.Service
import java.security.MessageDigest
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HashBuilder @Inject constructor(
) {


    private val messageDigest = MessageDigest.getInstance("SHA-256")


    private fun generateDataSetHash(dataSet: DataSet?): DataSet {
        var rawData = String()
        dataSet!!.credentials.let {
            it?.forEach { cre ->
                if (cre.innerHashValue.isNullOrEmpty())
                    cre.innerHashValue = makeHash(cre)?.innerHashValue
                rawData += cre.innerHashValue
            }
        }
        val message: ByteArray = rawData.toByteArray()
        val finalResult =
            Base64.getEncoder().encodeToString(messageDigest.digest(message)).replace("/", "")
        return dataSet.copy(hashData = finalResult)

    }

    private fun generateCredentialsHash(credentials: Credentials): Credentials {
        var hashData = credentials.innerHashValue
        if (hashData == null) {
            val message: ByteArray = (credentials.data + credentials.hint).toByteArray()
            hashData = Base64.getEncoder().encodeToString(messageDigest.digest(message))
        }
        return credentials.copy(innerHashValue = hashData)

    }

    private fun generateServiceHash(service: Service?): Service? {
        if (service == null) return null
        var hashData = service.hash
        service.dataSets.let {
            it?.forEach { dataSet ->
                if (dataSet.hashData.isNullOrEmpty())
                    dataSet.hashData = makeHash(dataSet)?.hashData
                hashData += dataSet.hashData
            }
        }
        val message: ByteArray = hashData.toByteArray()
        val finalResult = Base64.getEncoder().encodeToString(messageDigest.digest(message))
        return service.copy(hash = finalResult)

    }

    @Suppress("UNCHECKED_CAST")
    fun <T> makeHash(target: T): T? {
        return when (target) {
            is DataSet -> generateDataSetHash(target) as T
            is Credentials -> generateCredentialsHash(target) as T
            is Service -> generateServiceHash(target) as T
            else -> null
        }
    }
}