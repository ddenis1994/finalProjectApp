package com.example.finalprojectapp


import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.example.finalprojectapp.credentialsDB.LocalServiceDao
import com.example.finalprojectapp.credentialsDB.CredentialsDataBase
import com.example.finalprojectapp.credentialsDB.model.Credentials
import com.example.finalprojectapp.credentialsDB.model.DataSet
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class DataBaseUnitTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val db = Room.inMemoryDatabaseBuilder(
        context, CredentialsDataBase::class.java
    ).build()
    private val serverDAO: LocalServiceDao = db.credentialsDao()
    private val credentials = Credentials(data = "test", hint = listOf())
    private val dataSet= DataSet().copy(credentials = listOf(credentials))

    @AfterEach
    fun clearDataBase() = runBlocking {
        db.close()
    }

    @Nested
    @DisplayName("Credentials")
    inner class CredentialsTest {


        //region Insert Credentials
        @Nested
        @DisplayName("insert Credentials")
        inner class InsertCredentials {

            @DisplayName("test one Insert")
            @Test
            fun test1() = runBlocking {
                serverDAO.publicInsertCredentials(credentials.copy())
                val i = serverDAO.privateGetAllCredentials()
                assertEquals(1, i.size)
            }

            @DisplayName("test one Insert with encryption")
            @Test
            fun test2() = runBlocking {
                val data = "dataToTest"
                serverDAO.publicInsertCredentials(credentials.copy(data = data))
                val i = serverDAO.privateGetAllCredentials()
                assertNotEquals(data, i[0].data)

            }

            @DisplayName("test 2 different Credentials insert")
            @Test
            fun test2InsertCredentials() = runBlocking {
                serverDAO.publicInsertCredentials(credentials)
                serverDAO.publicInsertCredentials(credentials.copy(data = "2"))
                val i = serverDAO.privateGetAllCredentials()

                assertEquals(2, i.size)
            }

            @DisplayName("test 2 same Credentials insert")
            @Test
            fun test2SameInsertCredentials() = runBlocking {

                serverDAO.publicInsertCredentials(credentials)
                val g = serverDAO.publicInsertCredentials(credentials)
                val i = serverDAO.privateGetAllCredentials()

                assertEquals(-1, g)
                assertEquals(1, i.size)
            }

            @DisplayName("test 2  Credentials insert as a list")
            @Test
            fun test2CredentialsAsList() = runBlocking {
                val listCredentials = listOf(credentials.copy(), credentials.copy(data = "2"))
                val g = serverDAO.publicInsertArrayCredentials(listCredentials)
                val i = serverDAO.privateGetAllCredentials()
                assertEquals(2, g.size)
                assertEquals(2, i.size)
            }

            @DisplayName("insert Credentials with hash value")
            @Test
            fun testWithHashValue() = runBlocking {
                val oldHashValue = "old"
                serverDAO.publicInsertCredentials(credentials.copy(innerHashValue = oldHashValue))
                val i = serverDAO.privateGetAllCredentials()
                assertEquals(oldHashValue, i[0].innerHashValue)
            }
        }
        //endregion

        //region Query Credentials
        @Nested
        @DisplayName("Query Credentials")
        inner class QueryCredentials {

            @DisplayName("insert Credentials and than query it")
            @Test
            fun testQueryResult() = runBlocking {
                val data = "old"
                val dataSetId = 1L
                serverDAO.publicInsertCredentials(
                    credentials.copy(
                        data = data,
                        dataSetId = dataSetId
                    )
                )
                val result = serverDAO.publicGetCredentialsByDataSet(dataSetId)
                assertEquals(dataSetId, result[0].dataSetId)
                assertEquals(data, result[0].data)

            }
        }

        //endregion

    }

    @Nested
    @DisplayName("DataSet")
    inner class DataSetTest {

        @Test
        fun insertDataSet()= runBlocking {
            val result=serverDAO.publicInsertDataSet(dataSet.copy(credentials = listOf(credentials,credentials.copy(data = "12"))))
            val result2=serverDAO.publicInsertDataSet(dataSet.copy(credentials = listOf(credentials,credentials.copy(data = "2"))))
            val h=serverDAO.privateGetAllDataSetCredentialsManyToMany()
            assertEquals(0, result)
        }
    }


}