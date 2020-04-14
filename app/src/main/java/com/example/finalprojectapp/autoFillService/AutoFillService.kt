@file:Suppress("DEPRECATION")

package com.example.finalprojectapp.autoFillService

import android.R
import android.annotation.SuppressLint
import android.app.assist.AssistStructure
import android.content.Context
import android.os.CancellationSignal
import android.service.autofill.*
import android.util.Log
import android.view.autofill.AutofillValue
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import androidx.security.crypto.MasterKeys
import com.example.finalprojectapp.crypto.CredentialEncrypt
import com.example.finalprojectapp.crypto.DataHashGenerate
import com.example.finalprojectapp.crypto.EncryptLocalData
import com.example.finalprojectapp.data.model.Credentials
import com.example.finalprojectapp.data.model.Service
import com.example.finalprojectapp.localDB.PasswordRoomDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.System.load
import java.security.KeyStore
import java.util.*
import javax.crypto.Cipher


class AutoFillService : AutofillService() , LifecycleOwner {

    private lateinit var lifecycleRegistry: LifecycleRegistry

    override fun onFillRequest(request: FillRequest, cancellationSignal: CancellationSignal,
                               callback: FillCallback) {
        val context: List<FillContext> = request.fillContexts
        val structure: AssistStructure = context[context.size - 1].structure

        val myParser= ClientParser(structure, applicationContext as Context)

        myParser.parseForFill()

        val autoFillFields = myParser.autoFillFields

        myParser.result.observe(this, Observer {service ->
            val responseBuilder = FillResponse.Builder()
            if(service!=null) {
                val presentation = AutofillHelper
                        .newRemoteViews(packageName, "tap to sing in", R.drawable.ic_lock_lock)
                val dataSet = Dataset.Builder()
                autoFillFields.allAutofillHints.forEachIndexed { index, hintFromAndroid ->
                        service.credentials?.forEach {cre ->
                            cre.hint.forEach { hintFromLocalDB ->
                                if (hintFromAndroid == hintFromLocalDB)
                                    dataSet.setValue(
                                        autoFillFields.autofillIds[index],
                                        AutofillValue.forText(cre.data),
                                        presentation
                                    )
                        }
                    }
                }
                responseBuilder.addDataset(dataSet.build())
            }
            else if(autoFillFields.autofillIds.size>0) {
                responseBuilder.setSaveInfo(
                        SaveInfo.Builder(
                                SaveInfo.SAVE_DATA_TYPE_USERNAME or SaveInfo.SAVE_DATA_TYPE_PASSWORD or SaveInfo.SAVE_DATA_TYPE_EMAIL_ADDRESS,
                                autoFillFields.autofillIds.toTypedArray()
                        ).build())
            }
            if(autoFillFields.autofillIds.size>0)
                callback.onSuccess(responseBuilder.build())
            else
                callback.onFailure("cannot sort this out")
        })

        }

        @SuppressLint("RestrictedApi")
        override fun onSaveRequest(request: SaveRequest, callback: SaveCallback) {
            val context = request.fillContexts
            val structure = context[context.size - 1].structure

            val parser=ClientParser(structure, applicationContext as Context)
            parser.parseForSave()
            GlobalScope.launch {
                addDataToLocal(parser.autoFillDataForSaveList,parser.packageClientName())
            }
            callback.onSuccess()
        }

    private suspend fun addDataToLocal(autoFillDataForSaveList: MutableList<AutoFillNodeData>,request:String) {
        val localDB=PasswordRoomDatabase.getDatabase(applicationContext)
        val listOfCredentials= mutableListOf<Credentials>()
        autoFillDataForSaveList.forEach {
            it.textValue?.let { it1 ->
                Credentials(it.autofillHints?.toList()!!,
                    it1
                )
            }?.let { it2 -> listOfCredentials.add(it2) }
        }
        if (listOfCredentials.isNotEmpty()) {
            val service = Service(request, "", null, null, listOfCredentials)
            val finalService=service.copy(hashData = DataHashGenerate().generateSHA256(service))

            val localCredentials= mutableListOf<Credentials>()
            finalService.credentials?.forEach {
                val temp=EncryptLocalData().encrypt(it.data)
                localCredentials.add(it.copy(data = temp.first,iv = temp.second))
            }
            localDB.localCredentialsDAO()
                .insertSingleServiceCredentials(finalService.copy(credentials = localCredentials))

            addDataToRemote(finalService)
        }
    }

    private fun addDataToRemote(service: Service) {

        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser!!
        val encrypted = CredentialEncrypt("password")
        db.collection("users").document(user.uid)
            .collection("services").document(service.name)
            .set(service.copy(credentials = encrypted.encryptAll(service.credentials)))
    }

    override fun getLifecycle(): Lifecycle {
        return lifecycleRegistry
    }

    override fun onConnected() {
        lifecycleRegistry = LifecycleRegistry(this)
        lifecycleRegistry.markState(Lifecycle.State.CREATED)
        lifecycleRegistry.markState(Lifecycle.State.STARTED)
    }

    override fun onDisconnected() {
        lifecycleRegistry.markState(Lifecycle.State.DESTROYED)
    }

}