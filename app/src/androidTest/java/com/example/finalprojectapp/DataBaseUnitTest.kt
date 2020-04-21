package com.example.finalprojectapp


import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.example.finalprojectapp.credentialsDB.LocalServiceDao
import com.example.finalprojectapp.credentialsDB.CredentialsDataBase
import com.example.finalprojectapp.credentialsDB.model.Credentials
import com.example.finalprojectapp.credentialsDB.model.DataSet
import com.example.finalprojectapp.credentialsDB.model.Service
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class DataBaseUnitTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val db = Room.inMemoryDatabaseBuilder(
        context, CredentialsDataBase::class.java
    ).build()
    private val serverDAO: LocalServiceDao = db.serviceDao()
    private val credentials = Credentials(data = "test", hint = listOf())
    private val dataSet= DataSet().copy(credentials = listOf(credentials))
    private val service= Service().copy(dataSets = listOf(dataSet))

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

                val first =serverDAO.publicInsertCredentials(credentials)
                val g = serverDAO.publicInsertCredentials(credentials)
                val i = serverDAO.privateGetAllCredentials()

                assertEquals(first, g)
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


    }

    @Nested
    @DisplayName("Service")
    inner class DataSetTest {

        @Test
        @DisplayName("insert Service")
        fun insertDataSet()= runBlocking {
           serverDAO.publicInsertService(Service().copy(dataSets = listOf(dataSet.copy(credentials = listOf(credentials,credentials.copy(data = "8"))))))
            serverDAO.publicInsertService(Service().copy(name = "why",dataSets = listOf(dataSet.copy(credentials = listOf(credentials,credentials.copy(data = "2"),credentials.copy(data = "18"))))))
            serverDAO.publicInsertService(Service().copy(name = "why",dataSets = listOf(dataSet.copy(credentials = listOf(credentials,credentials.copy(data = "3"))))))
            val allData=serverDAO.publicGetAllService()
            assertEquals(2, allData.size)
        }

        @Test
        @DisplayName("insert single Service")
        fun insertSingleService()= runBlocking {
            val result=serverDAO.publicInsertService(service)
            assertNotEquals(result.first,-1L)
            val allData=serverDAO.publicGetAllService()
            assertEquals(1,allData.size)
        }

        @Test
        @DisplayName("insert 2 same Service")
        fun insert2Service()= runBlocking {
            serverDAO.publicInsertService(service)
            serverDAO.publicInsertService(service)
            val allData=serverDAO.publicGetAllService()
            assertEquals(1,allData.size)
        }


        @Test
        @DisplayName("insert 2 Different Service")
        fun insert2DifferentService()= runBlocking {
            serverDAO.publicInsertService(service.copy(name = "test"))
            serverDAO.publicInsertService(service)
            val allData=serverDAO.publicGetAllService()
            assertEquals(2,allData.size)
        }


        @Test
        @DisplayName("insert 1  Service with diffrent cradenitial")
        fun insertOneServiceWith2Diffrent()= runBlocking {
            serverDAO.publicInsertService(service.copy())
            serverDAO.publicInsertService(service.copy(dataSets = listOf(dataSet.copy(credentials = listOf(credentials.copy(data = "new")))) ))
            val allData=serverDAO.publicGetAllService()
            assertEquals(1,allData.size)
            assertEquals(2,allData[0].dataSets!!.size)
        }


        @Test
        @DisplayName("update one data set with one service")
        fun updateOneDataSetServiceWith2Diffrent()= runBlocking {
            val serviceName="service"
            val username="username"
            val oldCredentials=credentials.copy(data = "old",hint = listOf("password"))
            val service2=service.copy(name = serviceName,dataSets = listOf(dataSet.copy(credentials = listOf(oldCredentials,credentials.copy(data = username)))))
            val newCredentials=oldCredentials.copy(data = "new")
            serverDAO.publicInsertService(service2)
            serverDAO.publicInsertService(service2.copy(name = "test"))
            val t=serverDAO.publicGetUnionServiceNameAndCredentialsHash(service2,oldCredentials,newCredentials)
            val allData=serverDAO.publicGetAllService()
            assertEquals(2,allData.size)
            assertEquals(1,t)
        }



    }



}