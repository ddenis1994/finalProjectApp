package com.example.finalprojectapp.crypto

import androidx.test.platform.app.InstrumentationRegistry
import com.example.finalprojectapp.data.model.Credentials
import com.example.finalprojectapp.data.model.DataSet
import com.example.finalprojectapp.data.model.Service
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
internal class CryptographyTest{
    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    private val credentials = Credentials(
        data = "test",
        hint = listOf()
    )

    private val hashBuilder= HashBuilder()

    private val cre = LocalCryptography(hashBuilder)

    private val dataSet= DataSet().copy(credentials = listOf(credentials))
    private val service=Service().copy(dataSets = listOf(dataSet))


    @DisplayName("test encrypt Credentials")
    @Test
    fun testCredentialEncrypt() = runBlocking {


        hashBuilder.makeHash(Credentials())
        val result=cre.localEncrypt(credentials.copy())
        if (result != null) {
            assertNotEquals(credentials.data,result.data)
        }
        if (result != null) {
            assertNotEquals(credentials.iv,result.iv)
        }
        if (result != null) {
            assertFalse(result.innerHashValue.isNullOrEmpty())
        }
    }

    @DisplayName("test decrypt Credentials")
    @Test
    fun testCredentialDecrypt() = runBlocking {
        val result=cre.localDecryption(cre.localEncrypt(credentials.copy()))
        assertNotNull(result)
        assertEquals(credentials.data,result!!.data)
        assertFalse(result.innerHashValue.isNullOrEmpty())
        assertNull(result.iv)
    }

    @DisplayName("test local encryption data set")
    @Test
    fun testDataSetEncrypt() = runBlocking {
        val result=cre.localEncrypt(dataSet.copy())
        assertNotNull(result)
        assertNotNull(result!!.credentials)
        for ( i:Int in result.credentials!!.indices){
            assertNotEquals(result.credentials!![i].data, dataSet.credentials!![i].data)
        }


    }

    @DisplayName("test local decryption data set")
    @Test
    fun testDataSetDecryption() = runBlocking {
        val result=cre.localDecryption(cre.localEncrypt(dataSet.copy()))
        assertNotNull(result)
        assertNotNull(result!!.credentials)
        for ( i:Int in result.credentials!!.indices){
            assertEquals(result.credentials!![i].data, dataSet.credentials!![i].data)
        }
    }


}