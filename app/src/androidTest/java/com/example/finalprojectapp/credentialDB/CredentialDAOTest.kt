package com.example.finalprojectapp.credentialDB

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.example.finalprojectapp.credentialsDB.LocalDataBase
import com.example.finalprojectapp.data.model.Credentials
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class CredentialDAOTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val db = Room.inMemoryDatabaseBuilder(
        context, LocalDataBase::class.java
    ).build()
    private val credentialDAO = db.credentialDAO()

    private val credentials = Credentials(
        data = "test",
        innerHashValue = "",
        hint = listOf()
    )


    @AfterEach
    fun clearDataBase() = runBlocking {
        db.close()
    }

    @DisplayName("delete from credentials by data set")
    @Test
    fun  deleteCredentialByDataSetID(): Unit = runBlocking{
        val dataSetIDDelete=3L
        credentialDAO.insertCredentials(
            credentials.copy(),
            credentials.copy(credentialDataSetId = dataSetIDDelete,innerHashValue = "$dataSetIDDelete")
        )
        val i = credentialDAO.getAllCredentials()
        assertEquals(2, i.size)
        credentialDAO.deleteCredentialByDataSetID(dataSetIDDelete)
        assertEquals(1, credentialDAO.getAllCredentials().size)
        assertNotEquals(dataSetIDDelete, credentialDAO.getAllCredentials()[0].credentialDataSetId)
        assertEquals(0, credentialDAO.getAllCredentials()[0].credentialDataSetId)

    }


    @DisplayName("test one Insert")
    @Test
    fun testGetAllCredentials() = runBlocking {
        credentialDAO.insertCredentials(credentials.copy())
        val i = credentialDAO.getAllCredentials()
        assertEquals(1, i.size)
    }

    @DisplayName("test 2 different Credentials insert")
    @Test
    fun testGetAllCredentials2() = runBlocking {
        credentialDAO.insertCredentials(credentials)
        credentialDAO.insertCredentials(credentials.copy(data = "2",innerHashValue = "1"))
        val i = credentialDAO.getAllCredentials()

        assertEquals(2, i.size)
    }

    @DisplayName("test 2 same Credentials insert")
    @Test
    fun testUniqueCredentials() = runBlocking {

        val first =  credentialDAO.insertCredentials(credentials)
        val second =  credentialDAO.insertCredentials(credentials)
        val i = credentialDAO.getAllCredentials()

        assertNotEquals(first[0], second[0])
        assertEquals(second[0],-1L)
        assertEquals(1, i.size)
    }

    @DisplayName("test 2  Credentials insert as a list")
    @Test
    fun test2CredentialsAsList() = runBlocking {
        val listCredentials = arrayOf(credentials.copy(), credentials.copy(data = "2",innerHashValue = "2"))
        val g = credentialDAO.insertCredentials(*listCredentials)
        val i =  credentialDAO.getAllCredentials()
        assertEquals(2, g.size)
        assertEquals(2, i.size)
    }

    @DisplayName("insert Credentials with hash value")
    @Test
    fun testWithHashValue() = runBlocking {
        val oldHashValue = "old"
        credentialDAO.insertCredentials(credentials.copy(innerHashValue = oldHashValue))
        val i =  credentialDAO.getAllCredentials()
        assertEquals(oldHashValue, i[0].innerHashValue)
    }

    @DisplayName("update credentials")
    @Test
    fun testUpdateCredentials() = runBlocking {
        val newData="new data"
        credentialDAO.insertCredentials(credentials.copy())
        val i = credentialDAO.getAllCredentials()
        assertEquals(i.size,1)
        credentialDAO.updateCredentials(i[0].copy(data =newData ))
        val newQuery=credentialDAO.getAllCredentials()
        assertEquals(newQuery.size,1)
        assertEquals(newQuery[0].data,newData)

    }

    @DisplayName("query with null salt")
    @Test
    fun testGetCredentialsWithSalt() = runBlocking {
        val salt="ok"
        val toInsert= arrayOf(credentials,credentials.copy(salt = salt,data = salt,innerHashValue = "a"))
        credentialDAO.insertCredentials(*toInsert)
        val i =  credentialDAO.getCredentialsWithSalt()
        assertEquals(i.size,1)
        assertEquals(i[0].salt, salt)
    }
    @DisplayName("delete all test")
    @Test
    fun testDeleteAllCredentials() = runBlocking {
        val toInsert= arrayOf(credentials,credentials.copy(innerHashValue = "a"))
        credentialDAO.insertCredentials(*toInsert)
        val i =  credentialDAO.getAllCredentials()
        assertEquals(2,i.size)
        credentialDAO.deleteAllCredentials()
        assertEquals(0,credentialDAO.getCredentialsWithSalt().size)
    }

    @DisplayName("get credential by id")
    @Test
    fun testGetCredentialsByID() = runBlocking {
        val toInsert= arrayOf(credentials,credentials.copy(innerHashValue = "a"))
        credentialDAO.insertCredentials(*toInsert)
        val i =  credentialDAO.getAllCredentials()
        assertEquals(2,i.size)
        val result=credentialDAO.getCredentialsByID(1L)
        assertNotNull(result)
        assertEquals(1,credentialDAO.getCredentialsByID(1L)?.credentialsId)
        assertNull(credentialDAO.getCredentialsByID(3L))
    }

    @DisplayName("get credential by hash")
    @Test
    fun testGetCredentialsByHashData() = runBlocking {
        val hash="a"
        val toInsert= arrayOf(credentials,credentials.copy(innerHashValue = "a"))
        credentialDAO.insertCredentials(*toInsert)
        val i =  credentialDAO.getAllCredentials()
        assertEquals(2,i.size)
        val result=credentialDAO.getCredentialsByHashData(hash)
        assertNotNull(result)
        assertEquals(hash,credentialDAO.getCredentialsByHashData(hash)?.innerHashValue)

    }

    @DisplayName("delete credential")
    @Test
    fun testDeleteCredential() = runBlocking {
        val hash="a"
        val toInsert= arrayOf(credentials,credentials.copy(innerHashValue = "a"))
        credentialDAO.insertCredentials(*toInsert)
        val i =  credentialDAO.getAllCredentials()
        assertEquals(2,i.size)
        val result=credentialDAO.getCredentialsByHashData(hash)
        assertNotNull(result)
        credentialDAO.deleteCredential(result!!)
        assertNull(credentialDAO.getCredentialsByHashData(hash))
    }



}