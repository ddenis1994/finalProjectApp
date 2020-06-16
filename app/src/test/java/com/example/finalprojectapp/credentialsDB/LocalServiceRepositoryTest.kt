package com.example.finalprojectapp.credentialsDB

import com.example.finalprojectapp.crypto.HashBuilder
import com.example.finalprojectapp.crypto.LocalCryptography
import com.example.finalprojectapp.data.model.Credentials
import com.example.finalprojectapp.data.model.DataSet
import com.example.finalprojectapp.data.model.Service
import com.example.finalprojectapp.data.model.relationship.ServiceWithDataSets
import io.mockk.coEvery
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockkClass
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith


@ExtendWith(MockKExtension::class)
class LocalServiceRepositoryTest {
    private val fakeServiceDao = mockkClass(ServiceDAO::class)
    private val fakeDataSetRepository = mockkClass(DataSetRepository::class)
    private val fakeLocalCryptography = mockkClass(LocalCryptography::class,relaxed = true)
    private val localServiceRepository =
        ServiceRepositoryLocal(fakeServiceDao, fakeDataSetRepository, fakeLocalCryptography)
    private val service = Service()
    private val dataSet = DataSet()
    private val credentials = Credentials(data = "credentials 1")

    @DisplayName("test insert service")
    @Test
    fun publicInsertService() = runBlocking {
        coEvery { fakeServiceDao.getServiceByName("") } returns ServiceWithDataSets(service, listOf(dataSet))
        coEvery { fakeDataSetRepository.getDataSetByID(0) } returns DataSet(credentials = listOf(credentials))
        every { fakeLocalCryptography.encrypt(service) } returns service
        val result1 = localServiceRepository.publicInsertService(service)
        assertNotNull(result1)
        coEvery { fakeServiceDao.getServiceByName("") } returns ServiceWithDataSets(service, listOf(dataSet))
        every { fakeLocalCryptography.encrypt(service) } returns service.copy(hash = "a")
        coEvery { fakeServiceDao.updateService(ServiceWithDataSets(service, listOf(dataSet)).service.copy(dataSets = service.dataSets))} returns Unit
        val result2 = localServiceRepository.publicInsertService(service)
        assertNotNull(result2)

//        coEvery { fakeServiceDao.getServiceByName("") } returns null
//        every { fakeLocalCryptography.encrypt(service) } returns service.copy(hash = "ab")
//        coEvery { fakeServiceDao.privateInsertService(service.copy(hash = "ab")) } returns 1L
//        every { fakeLocalCryptography.decryption(service ) } returns service
//        coEvery { fakeDataSetRepository.publicInsertDataSet(service.copy(dataSets = listOf(dataSet.copy(serviceId = 1L))).dataSets!![0] )} returns listOf()
//        val result2 = localServiceRepository.publicInsertService(service)
//        assertNotNull(result2)

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