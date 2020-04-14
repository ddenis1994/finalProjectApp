package com.example.finalprojectapp

import com.example.finalprojectapp.crypto.DataHashGenerate
import com.example.finalprojectapp.data.model.Credentials
import com.example.finalprojectapp.data.model.Service
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TestDataHashGenerate {
    private val testClass= DataHashGenerate()

    @Test
    fun `test with normal params`(){
        val testData=Service("testService","",null,null,
            listOf(Credentials(listOf("testHint1"),"testData")))
        val result=testClass.generateSHA256(testData)
        assertNotNull(result)
    }

    @Test
    fun `test 2 different input`(){
        val testData1=Service("testService","",null,null,
            listOf(Credentials(listOf("testHint1"),"testData")))
        val testData2=testData1.copy(credentials = listOf(testData1.credentials?.get(0)?.copy(data = "testData2")!!))
        val result1=testClass.generateSHA256(testData1)
        val result2=testClass.generateSHA256(testData2)
        assertNotEquals(result1,result2)
    }

    @Test
    fun `test with null params`() {
        val testData=Service("testService","",null,null,
            listOf(Credentials(listOf("testHint1"),"testData")))
        val result=testClass.generateSHA256(testData)
        assertNotNull(result)
    }

    @Test
    fun `test with 2 same services`() {
        val testData1=Service("testService","",null,null,
            listOf(Credentials(listOf("testHint1"),"testData")))
        val testData2=testData1.copy()
        val result1=testClass.generateSHA256(testData1)
        val result2=testClass.generateSHA256(testData2)
        assertEquals(result1,result2)
    }


}