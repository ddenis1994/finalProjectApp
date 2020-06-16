package com.example.finalprojectapp.credentialsDB

import com.example.finalprojectapp.crypto.LocalCryptography
import com.example.finalprojectapp.data.model.Credentials
import com.example.finalprojectapp.data.model.DataSet
import com.example.finalprojectapp.data.model.relationship.DataSetWithCredentials
import io.mockk.coEvery
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockkClass
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class DataSetRepositoryTest {

    private var fakeCredentialRepository = mockkClass(CredentialRepository::class)
    private var fakeDataSetDao = mockkClass(DataSetDAO::class)
    private var fakeLocalCryptography = mockkClass(LocalCryptography::class)
    private val dataSetRepository=DataSetRepository(fakeCredentialRepository,fakeDataSetDao,fakeLocalCryptography)

    @DisplayName("delete data set and credentials")
    @Test
    fun privateDeleteDataSet() = runBlocking {
        every { fakeCredentialRepository.deleteCredential(credentials = Credentials().copy(credentialsId = 1L)) } returns Unit
        coEvery {fakeDataSetDao.getDataSetWithCredentialsByDataSetID(1L) } returns DataSetWithCredentials()
        coEvery {fakeDataSetDao.deleteDataSet(DataSet().copy(dataSetId = 1L)) } returns Unit
        val result = dataSetRepository.publicDeleteCredential(1L, 1L)
        assertNotNull(result)
    }

    @DisplayName("Insert data set")
    @Test
    fun publicInsertDataSet() = runBlocking{
        val dataSet=DataSet(credentials = listOf(Credentials(credentialDataSetId = 1)))
        every { fakeLocalCryptography.encrypt(dataSet) } returns null
        val result1= dataSetRepository.publicInsertDataSet(dataSet)
        assertEquals(1,result1?.size)
        assertEquals(-1L, result1?.get(0)?.first)
        every { fakeLocalCryptography.encrypt(dataSet) } returns dataSet
        coEvery { fakeDataSetDao.privateInsertDataSet(dataSet) } returns listOf(-1L)
        coEvery { fakeDataSetDao.getDataSetByHash("") } returns DataSetWithCredentials(DataSet(),
            listOf(Credentials(credentialsId = 1L)))
        val result2=dataSetRepository.publicInsertDataSet(dataSet)
        assertEquals(1,result2?.size)
        coEvery { fakeDataSetDao.privateInsertDataSet(dataSet) } returns listOf(1L)
        coEvery { fakeCredentialRepository.insertCredentials(*dataSet.credentials!!.toTypedArray()) } returns listOf(1L)
        val result3=dataSetRepository.publicInsertDataSet(dataSet)
        assertEquals(1,result3?.size)
        val multiDataSet= listOf(dataSet,dataSet.copy(hashData = "2"))
        every { fakeLocalCryptography.encrypt(dataSet.copy(hashData = "2")) } returns dataSet.copy(hashData = "2")
        coEvery { fakeDataSetDao.privateInsertDataSet(dataSet.copy(hashData = "2")) } returns listOf(2L)
        coEvery { fakeCredentialRepository.insertCredentials(dataSet.credentials!![0].copy(credentialDataSetId = 2)) } returns listOf(2L)
        val result4=dataSetRepository.publicInsertDataSet( *multiDataSet.toTypedArray())
        assertEquals(2,result4?.size)



    }


}