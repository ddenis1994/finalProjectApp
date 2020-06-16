package com.example.finalprojectapp.credentialDB

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.example.finalprojectapp.credentialsDB.LocalDataBase
import com.example.finalprojectapp.data.model.Credentials
import com.example.finalprojectapp.data.model.DataSet
import com.example.finalprojectapp.data.model.Service
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ServiceDAOTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val db = Room.inMemoryDatabaseBuilder(
        context, LocalDataBase::class.java
    ).build()
    private val serviceDao = db.serviceDao()
    private val credentials = Credentials(
        data = "test",
        hint = listOf()
    )
    private val credentials1 = credentials.copy(innerHashValue = "1", data = "1")
    private val credentials2 = credentials.copy(innerHashValue = "2", data = "2")
    private val credentials3 = credentials.copy(innerHashValue = "3", data = "3")
    private val hash1 = "dataSet1"
    private val dataSet1 =
        DataSet(credentials = listOf(credentials1, credentials2), hashData = hash1, serviceId = 1L)
    private val hash2 = "dataSet2"
    private val dataSet2 =
        DataSet(credentials = listOf(credentials3), hashData = hash2, serviceId = 2L)
    private val dataSets = arrayOf(dataSet1, dataSet2)
    private val mainServiceName = "name"
    private val service =
        Service().copy(dataSets = dataSets.toList(), hash = "1", name = mainServiceName)


    @BeforeAll
    fun beforeAllTests() = runBlocking {
        serviceDao.privateInsertService(service)
        db.credentialDAO().insertCredentials(
            credentials.copy(
                credentialDataSetId = 1L,
                hint = listOf("password"),
                innerHashValue = "1"
            )
        )
        db.credentialDAO().insertCredentials(
            credentials.copy(
                credentialDataSetId = 2L,
                hint = listOf("pass"),
                innerHashValue = "2"
            )
        )
        db.dataSetDAO().privateInsertDataSet(*dataSets)
        val al = serviceDao.privateGetAllService()
        assertEquals(1, al.size)
    }

    @AfterAll
    fun afterAllTests() = runBlocking {
        serviceDao.deleteAllService()
        val al = serviceDao.privateGetAllService()
        assertEquals(0, al.size)
        db.close()
    }

    @DisplayName("delete service by id")
    @Test
    fun deleteService() = runBlocking {
        val result = serviceDao.privateInsertService(service.copy(hash = "toDelete"))
        assertEquals(2, serviceDao.privateGetAllService().size)
        serviceDao.deleteService(Service().copy(serviceId = result[0]))
        assertEquals(1, serviceDao.privateGetAllService().size)
    }

    @DisplayName("get service by name")
    @Test
    fun getServiceByName(): Unit = runBlocking {
        val result = serviceDao.getServiceByName(mainServiceName)
        assertNotNull(result)
        assertNotNull(result?.dataSets)
        assertEquals(1, result?.dataSets?.size)
    }

    @DisplayName("get service by dataSets id")
    @Test
    fun getServiceByDataSetId(): Unit = runBlocking {
        val result = serviceDao.getServiceByDataSetId(1L)
        assertNotNull(result)
        assertNotNull(result!!.dataSets)
        assertEquals(1, result.dataSets.size)
    }

    @DisplayName("get service by dataSets id")
    @Test
    fun findServiceAndDataSetsAndCredentials(): Unit = runBlocking {
        val result = serviceDao.findServiceAndDataSetsAndCredentials(1L)
        assertNotNull(result)

    }



}