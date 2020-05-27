package com.example.finalprojectapp.crypto

import com.example.finalprojectapp.data.model.Credentials
import com.example.finalprojectapp.data.model.DataSet
import com.example.finalprojectapp.data.model.Service
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName

internal class HashBuilderTest {

    private val hashBuilder = HashBuilder()

    private val credentials = Credentials(
        data = "test",
        hint = listOf()
    )
    private val dataSet = DataSet().copy(credentials = listOf(credentials))
    private val service = Service().copy(dataSets = listOf(dataSet))

    @DisplayName("test credential Hash Generate")
    @Test
    fun testCredentialHash() = runBlocking {
        val result = hashBuilder.makeHash(credentials)
        assertNotNull(result)
        assertTrue(!result!!.innerHashValue.isNullOrEmpty())
    }

    @DisplayName("test data set hash Generate")
    @Test
    fun testDataSetHash() = runBlocking {
        val result = hashBuilder.makeHash(dataSet)
        assertTrue(!result!!.hashData.isNullOrEmpty())
    }

    @DisplayName("test that backslash not in data set hash Generate")
    @Test
    fun testDataSetBackSlashNotInHashHash() = runBlocking {
        val result = hashBuilder.makeHash(dataSet)
        assertTrue(!result!!.hashData.isNullOrEmpty())
        assertTrue(!result.hashData!!.contains("/"))

    }

    @DisplayName("test service hash Generate")
    @Test
    fun testServiceHash() = runBlocking {
        val result = hashBuilder.makeHash(service)
        assertTrue(result!!.hash.isNotEmpty())
    }
}