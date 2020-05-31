package com.example.finalprojectapp

import android.content.SharedPreferences
import android.util.Log
import com.example.finalprojectapp.credentialsDB.ServiceRepository
import com.example.finalprojectapp.crypto.HashBuilder
import com.example.finalprojectapp.crypto.MasterPassword
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith


@ExtendWith(MockKExtension::class)
internal class MasterPasswordTest {

    //val context = ApplicationProvider.getApplicationContext<Context>()
    private val password = "Password"
    private val hashBuilder = HashBuilder()

    @MockK
    lateinit var setting: SharedPreferences

    @MockK
    lateinit var fakeServiceRepository: ServiceRepository


    @Test
    fun setPassword() {
        every { fakeServiceRepository.updateRemotePassword(password) } returns true
        every { setting.edit().putString("mainPassword", password).apply() } returns Unit
        every { setting.getString("mainPassword", "") } returns password
        every { fakeServiceRepository.updateRemotePassword( hashBuilder.makeHash(password)) } returns true


        val masterPassword = MasterPassword(setting, fakeServiceRepository, hashBuilder)
        val h = fakeServiceRepository.updateRemotePassword(password)
        assertTrue(h)
        masterPassword.setPassword(password)


    }

    @Test
    fun checkForLocalPassword() {
    }
}