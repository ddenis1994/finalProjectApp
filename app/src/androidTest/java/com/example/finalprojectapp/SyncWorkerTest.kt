package com.example.finalprojectapp

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.work.Data
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import com.example.finalprojectapp.workers.SaveDataOrganizeWorker
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SyncWorkerTest {
    private val context: Context= ApplicationProvider.getApplicationContext()

    @Test
    fun testSyncWorkerWithoutService() {

        val worker = TestListenableWorkerBuilder<SaveDataOrganizeWorker>(context = context,
        inputData = Data.Builder()
            .putString("data","test")
            .build()
        )
            .build()
        runBlocking {
            val result = worker.doWork()
            assertThat(result,`is`( ListenableWorker.Result.failure(Data.Builder()
                .putString("reason", "cannot find service name")
                .build())))
        }
    }
    @Test
    fun testSyncWorkerWithoutData() {
        val worker = TestListenableWorkerBuilder<SaveDataOrganizeWorker>(context = context,
            inputData = Data.Builder()
                .putString("serviceRequest","test")
                .build()
        )
            .build()
        runBlocking {
            val result = worker.doWork()
            assertThat(result,`is`( ListenableWorker.Result.failure(Data.Builder()
                .putString("reason", "cannot find service data")
                .build())))
        }
    }

}