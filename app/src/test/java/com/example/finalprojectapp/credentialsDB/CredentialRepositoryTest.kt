package com.example.finalprojectapp.credentialsDB

import com.example.finalprojectapp.crypto.LocalCryptography
import com.example.finalprojectapp.data.model.Credentials
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
internal class CredentialRepositoryTest {
    private var credentialDAO: CredentialDAO = mockkClass(CredentialDAO::class)
    private var localCryptography: LocalCryptography = mockkClass(LocalCryptography::class)
    private val credentialsLocalRepository = CredentialRepository(credentialDAO, localCryptography)
    private val credentials: Credentials = Credentials(innerHashValue = "")



    @DisplayName("test Insert credentials")
    @Test
    fun publicInsertCredentials() = runBlocking {

        assertEquals(
            listOf(-1L),
            credentialsLocalRepository.insertCredentials(credentials.copy(salt = "a"))
        )
        every { localCryptography.encrypt(credentials) } returns credentials
        coEvery { credentialDAO.insertCredentials(credentials) } returns listOf(1L)
        val result=credentialsLocalRepository.insertCredentials(credentials)
        assertEquals(result, listOf(1L))
        coEvery { credentialDAO.insertCredentials(credentials) } returns listOf(-1L)
        coEvery { credentialDAO.getCredentialsByHashData("")} returns credentials.copy(credentialsId = 2L)
        val result2=credentialsLocalRepository.insertCredentials(credentials)
        assertEquals(result2, listOf(2L))
        assertTrue(true)


    }

    @Test
    fun publicGetCredentialsID() = runBlocking{
        coEvery { credentialDAO.getCredentialsByID(0L)} returns credentials
        every { localCryptography.decryption(credentials) } returns credentials
        val result=credentialsLocalRepository.publicGetCredentialsID(0L)
        assertNotNull(result)

    }

}