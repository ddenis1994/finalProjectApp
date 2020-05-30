/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.finalprojectapp.utils

import android.content.Context
import androidx.fragment.app.Fragment
import com.example.finalprojectapp.MainActivity
import com.example.finalprojectapp.credentialsDB.*
import com.example.finalprojectapp.crypto.HashBuilder
import com.example.finalprojectapp.crypto.LocalCryptography

import com.example.finalprojectapp.ui.credentials.CredentialsViewModelFactory
import com.example.finalprojectapp.ui.credentials.inner.CredentialInnerViewModelFactory
import com.example.finalprojectapp.ui.dashboard.DashboardViewModelFactory
import com.example.finalprojectapp.ui.notifications.NotificationViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job


/**
 * Static methods used to inject classes needed for various Activities and Fragments.
 */
object InjectorUtils {

    private fun getMainRepository(context: Context): ServiceRepository {
        return ServiceRepository(context, ServiceRepositoryLocal( LocalDataBase.getDatabase(context.applicationContext).serviceDao(),
            DataSetRepository(CredentialRepository(LocalDataBase.getDatabase(context.applicationContext).credentialDAO(),
                LocalCryptography(HashBuilder())
            ),LocalDataBase.getDatabase(context.applicationContext).dataSetDAO(),LocalCryptography(HashBuilder()
        ))),NotificationRepository(LocalDataBase.getDatabase(context.applicationContext).notificationDao(),
            CoroutineScope(Job()+Dispatchers.Default)))

    }

    private fun getNotificationRepository(context: Context): NotificationRepository {

        //TODO fix injection
        return NotificationRepository.getInstance(
            LocalDataBase.getDatabase(context.applicationContext).notificationDao(), CoroutineScope(
                Job())
        )
    }

    fun provideDashboardViewModelFactory(
        fragment: Fragment
    ): DashboardViewModelFactory {
        val repository = getMainRepository(fragment.requireContext())
        return DashboardViewModelFactory(repository)
    }


    fun provideNotificationViewModelFactory(
        fragment: Fragment
    ): NotificationViewModelFactory {
        val repository = getNotificationRepository(fragment.requireContext())
        return NotificationViewModelFactory(repository)
    }

}
