package com.example.finalprojectapp.crypto

import com.example.finalprojectapp.data.model.Credentials
import com.example.finalprojectapp.data.model.DataSet
import com.example.finalprojectapp.data.model.Notification
import com.example.finalprojectapp.data.model.Service
import java.security.MessageDigest
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HashBuilder @Inject constructor(
) {


    private val messageDigest = MessageDigest.getInstance("SHA-256")
    private val encoder=Base64.getEncoder()

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
        credentials.iv?.let {
            if (it.isNotEmpty())
                return credentials
        }

        val hashData: String?
        val message: ByteArray = (credentials.data + credentials.hint).toByteArray()
        hashData = Base64.getEncoder().encodeToString(messageDigest.digest(message))
        return credentials.copy(innerHashValue = hashData)

    }

    private fun generateServiceHash(service: Service?): Service? {
        if (service == null) return null
        var hashData = service.hash
        service.dataSets.let {
            it?.forEach { dataSet ->
                if (dataSet.hashData.isEmpty())
                    dataSet.hashData = makeHash(dataSet)?.hashData ?: return null
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
            is String -> generateStringHash(target) as T
            is Notification -> generateServiceNotification(target) as T
            else -> null
        }
    }

    private fun generateStringHash(target: String?): String? {
        var hash=target ?: return null
        return encoder.encodeToString(messageDigest.digest(hash.toByteArray()))

    }

    private fun generateServiceNotification(target: Notification?): Notification? {
        if (target == null) return null
        var hashData = target.hash
        if (hashData.isEmpty()) {
            val message: ByteArray =
                (target.mainMassage + target.secondMassage + target.time).toByteArray()
            hashData = Base64.getEncoder().encodeToString(messageDigest.digest(message))
        }
        return target.copy(hash = hashData)
    }
}