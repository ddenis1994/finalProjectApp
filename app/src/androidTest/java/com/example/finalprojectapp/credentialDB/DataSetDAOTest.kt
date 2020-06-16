package com.example.finalprojectapp.credentialDB

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.example.finalprojectapp.credentialsDB.LocalDataBase
import com.example.finalprojectapp.data.model.Credentials
import com.example.finalprojectapp.data.model.DataSet
import com.example.finalprojectapp.util.InstantExecutorExtension
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.extension.ExtendWith

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(InstantExecutorExtension::class)
class DataSetDAOTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val db = Room.inMemoryDatabaseBuilder(
        context, LocalDataBase::class.java
    ).build()
    private val dataSetDao = db.dataSetDAO()
    private val credentials = Credentials(
        data = "test",
        hint = listOf()
    )
    private val credentials1 = credentials.copy(innerHashValue = "1", data = "1")
    private val credentials2 = credentials.copy(innerHashValue = "2", data = "2")
    private val credentials3 = credentials.copy(innerHashValue = "3", data = "3")
    private val hash1 = "dataSet1"
    private val dataSet1 =
        DataSet(credentials = listOf(credentials1, credentials2), hashData = hash1,serviceId = 1L)
    private val hash2 = "dataSet2"
    private val dataSet2 = DataSet(credentials = listOf(credentials3), hashData = hash2,serviceId = 2L)
    private val dataSets = arrayOf(dataSet1, dataSet2)

    @BeforeAll
    fun beforeAllTests() = runBlocking {
        dataSetDao.privateInsertDataSet(*dataSets)
        db.credentialDAO().insertCredentials(credentials.copy(credentialDataSetId = 1L,hint = listOf("password"),innerHashValue = "1"))
        db.credentialDAO().insertCredentials(credentials.copy(credentialDataSetId = 2L,hint = listOf("pass"),innerHashValue = "2"))
        val al = dataSetDao.publicGetAllDataSet()
        assertEquals(2, al.size)
    }

    @AfterAll
    fun afterAllTests() = runBlocking {
        dataSetDao.deleteAllDataSets()
        val al = dataSetDao.publicGetAllDataSet()
        assertEquals(0, al.size)
        db.close()
    }

    @DisplayName("get data set by hash")
    @Test
    fun privateGetDataSetByHash() = runBlocking {
        val result = dataSetDao.getDataSetByHash(hash1)
        assertNotNull(result)
        assertEquals(result!!.hashData, hash1)


    }



    @DisplayName("get data set by id")
    @Test
    fun getDataSetByDataSetID() = runBlocking{
        val result = dataSetDao.getDataSetByDataSetID(1L)
        assertNotNull(result)
        assertEquals(result!!.hashData, hash1)
    }
    @DisplayName("get all dataSet with credentials")
    @Test
    fun privateGetDataSetToCredentials() = runBlocking{
        val allData=dataSetDao.getDataSetWithCredentials()
        assertEquals(2,allData.size)
        val request=allData.first { it.dataSet.dataSetId==1L }.credentials
        assertEquals(1,request.size)

    }


    @DisplayName("deleteDataSet")
    @Test
    fun deleteDataSet() = runBlocking(){
        val hashDelete="delete"
        dataSetDao.privateInsertDataSet(DataSet(hashData = hashDelete))
        assertEquals(3,dataSetDao.publicGetAllDataSet().size)
        val toDelete=dataSetDao.getDataSetByHash(hashDelete)
        assertNotNull(toDelete)
        dataSetDao.deleteDataSet(toDelete!!)
    }

    @DisplayName("find all passwords in dataSetCredentials")
    @Test
    fun publicGetAllHashCredentials() {
        val result=dataSetDao.publicGetAllHashCredentials2()
        assertNotNull(result)
    }

    @DisplayName("find all passwords in dataSetCredentials with id")
    @Test
    fun getDataSetWithCredentialsByDataSetID() {
        val result=dataSetDao.getDataSetWithCredentialsByDataSetID(1L)
        assertNotNull(result)
        assertEquals("1", result?.credentials?.get(0)!!.innerHashValue)
        val result2=dataSetDao.getDataSetWithCredentialsByDataSetID(2L)
        assertNotNull(result)
        assertEquals("2", result2?.credentials?.get(0)!!.innerHashValue)


    }


}