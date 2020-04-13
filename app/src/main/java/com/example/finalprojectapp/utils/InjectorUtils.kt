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
import com.example.finalprojectapp.data.viewModels.CredentialsViewModelFactory
import com.example.finalprojectapp.localDB.MainRepository
import com.example.finalprojectapp.localDB.PasswordRoomDatabase


/**
 * Static methods used to inject classes needed for various Activities and Fragments.
 */
object InjectorUtils {

    private fun getMainRepository(context: Context): MainRepository {
        return MainRepository.getInstance(
                PasswordRoomDatabase.getDatabase(context.applicationContext).localCredentialsDAO(),
                PasswordRoomDatabase.getDatabase(context.applicationContext).serviceDAO())
    }

    fun provideCredentialsViewModelFactory(
        fragment: Fragment
    ): CredentialsViewModelFactory {
        val repository = getMainRepository(fragment.requireContext())
        return CredentialsViewModelFactory(repository)
    }
}
