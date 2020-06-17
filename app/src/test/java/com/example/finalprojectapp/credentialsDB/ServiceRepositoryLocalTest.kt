package com.example.finalprojectapp.credentialsDB

import com.example.finalprojectapp.crypto.LocalCryptography
import com.example.finalprojectapp.data.model.Credentials
import com.example.finalprojectapp.data.model.DataSet
import com.example.finalprojectapp.data.model.Service
import com.example.finalprojectapp.data.model.relationship.ServiceWithDataSets
import io.mockk.*
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith


@ExtendWith(MockKExtension::class)
class ServiceRepositoryLocalTest {
    private val fakeServiceDao = mockkClass(ServiceDAO::class)
    private val fakeDataSetRepository = mockkClass(DataSetRepository::class)
    private val fakeLocalCryptography = mockkClass(LocalCryptography::class,relaxed = true)
    private val localServiceRepository =
        ServiceRepositoryLocal(fakeServiceDao, fakeDataSetRepository, fakeLocalCryptography)
    private val service = Service()
    private val dataSet = DataSet()
    private val credentials = Credentials(data = "credentials 1")


    @DisplayName("test update existing service")
    @Test
    fun updateExistedService(): Unit = runBlocking{
        coEvery { fakeDataSetRepository.publicInsertDataSet(any()) } returns listOf(Pair(-1L,null))
        coEvery { fakeServiceDao.getServiceByName(any()) } returns ServiceWithDataSets(service, listOf(dataSet))
        coEvery { fakeServiceDao.updateService(any())} just Runs
        coEvery { fakeDataSetRepository.getDataSetByID(any()) } returns DataSet(credentials = listOf(credentials))
        every { fakeLocalCryptography.decryption(service) } returns service
        val result=localServiceRepository.updateExistedService(service)
        assertNotNull(result)
    }

    @DisplayName("test get full service by name")
    @Test
    fun getServiceByName() = runBlocking {
        coEvery { fakeServiceDao.getServiceByName("") } returns null
        val result1 = localServiceRepository.getServiceByName("")
        assertNull(result1)
        coEvery { fakeServiceDao.getServiceByName("") } returns ServiceWithDataSets(
            service,
            listOf(dataSet)
        )
        coEvery { fakeDataSetRepository.getDataSetByID(0) } returns DataSet(
            credentials = listOf(
                credentials
            )
        )
        val result2 = localServiceRepository.getServiceByName("")
        assertNotNull(result2)
        assertEquals(1, result2!!.dataSets!!.size)
        assertEquals(1, result2.dataSets!![0].credentials!!.size)
    }

//    suspend fun publicGetAllServiceSuspand(): List<Service>
//
//
//    private suspend fun privateGetServiceByName(string: String): Service?

}