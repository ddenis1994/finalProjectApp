package com.example.finalprojectapp.ui.auth

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.service.autofill.FillResponse
import android.view.autofill.AutofillManager.EXTRA_AUTHENTICATION_RESULT
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.example.finalprojectapp.R
import com.example.finalprojectapp.utils.SingleEncryptedSharedPreferences
import java.util.concurrent.Executor

class AppAuthActivity : AppCompatActivity() {
    private lateinit var biometricManager:BiometricManager
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private lateinit var setting: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        val structure: FillResponse? = intent.getParcelableExtra("response")
        val dataSetId = intent.getIntExtra("dataSetId",-1)
        val test = intent.getIntExtra("target",-1)
        val returnIntent=Intent().apply {
            putExtra("target",test)
        }
        setResult(Activity.RESULT_OK,returnIntent)
        finish()

        setting =
            SingleEncryptedSharedPreferences().getSharedPreference(this)

        when (setting.getBoolean("SecondFactorAuthentication", false)) {
            true -> {

            }
            false -> {

            }
        }

        biometricManager = BiometricManager.from(this)
        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int,
                                                   errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(applicationContext,
                        "Authentication error: $errString", Toast.LENGTH_SHORT)
                        .show()
                    finish()
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    val replyIntent = Intent().apply {
                        // Send the data back to the service.
                        if(dataSetId !=-1)
                            putExtra("dataSetId",dataSetId)
                        else
                            putExtra(EXTRA_AUTHENTICATION_RESULT, structure)
                    }
                    setResult(RESULT_OK, replyIntent)
                    Toast.makeText(applicationContext,
                        "Authentication succeeded!", Toast.LENGTH_SHORT)
                        .show()
                    finish()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    val replyIntent = Intent().apply {
                        // Send the data back to the service.
                    }
                    setResult(RESULT_CANCELED, replyIntent)
                    Toast.makeText(applicationContext, "Authentication failed",
                        Toast.LENGTH_SHORT)
                        .show()
                    finish()
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for my app")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Use account password")
            .build()




        when (biometricManager.canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS ->
                biometricPrompt.authenticate(promptInfo)
            else->{
                setResult(RESULT_CANCELED)
                Toast.makeText(applicationContext, "Authentication failed",
                    Toast.LENGTH_SHORT)
                    .show()
                finish()
            }
        }

    }


}
