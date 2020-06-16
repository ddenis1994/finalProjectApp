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
        every {fakeDataSetDao.getDataSetWithCredentialsByDataSetID(1L) } returns DataSetWithCredentials()
       coEvery {fakeDataSetDao.deleteDataSet(DataSet().copy(dataSetId = 1L)) } returns Unit
        val result=dataSetRepository.publicDeleteCredential2(1L,1L)
        assertTrue(false)
    }

    @Test
    fun deleteDataSetById() {
    }

    @Test
    fun deleteAllDataSets() {
    }


    @Test
    fun publicInsertDataSet() {
    }

    @Test
    fun getDataSetByHash() {
    }

}