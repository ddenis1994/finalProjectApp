package com.example.finalprojectapp.ui.auth

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import com.example.finalprojectapp.MainApplication
import com.example.finalprojectapp.R
import com.example.finalprojectapp.di.MainComponent
import java.util.concurrent.Executor
import javax.inject.Inject


class AppAuthActivity : AppCompatActivity() {
    @Inject
    lateinit var biometricManager: BiometricManager

    @Inject
    lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    @Inject
    lateinit var setting: SharedPreferences
    private var target: Int = -1
    private lateinit var applicationComponent: MainComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applicationComponent = (application as MainApplication).getComponent()
        applicationComponent.uiComponent().create().authActivityComponent().create().inject(this)
        setContentView(R.layout.activity_auth)


        target = intent.getIntExtra("target", -1)

        val methodAuth = setting.getString("SecondFactorAuthentication", "")

        val authList = resources.getStringArray(R.array.second_factor)
        when (methodAuth) {
            //none
            authList[0] -> {
                authWithOK()
            }
            //pin
            authList[1] -> authWithPin()
            //password
            authList[2] -> authWithPassword()
            //fingerprint
            authList[3] -> authWithFinger()
            //camera
            authList[4] -> authWithCamera()
            else -> print("error")

        }

    }

    private fun authWithCamera() {
        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for my app")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Use account password")
            .setConfirmationRequired(false)
            .build()
        startBiometricAuth()
    }

    private fun startBiometricAuth() {
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(
                        applicationContext,
                        "Authentication error: $errString", Toast.LENGTH_SHORT
                    )
                        .show()
                    finish()
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    authWithOK()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    setResult(RESULT_CANCELED)
                    Toast.makeText(
                        applicationContext, "Authentication failed",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    finish()
                }
            })
        biometricPrompt.authenticate(promptInfo)

    }

    private fun authWithFinger() {
        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for my app")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Use account password")
            .build()
        startBiometricAuth()


    }

    private fun authWithPassword() {
        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for my app")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Use account password")
            .setDeviceCredentialAllowed(true)
            .build()
        startBiometricAuth()
    }

    private fun authWithPin() {
        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for my app")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Use account password")
            .setDeviceCredentialAllowed(true)
            .build()
        startBiometricAuth()
    }

    private fun authWithOK() {
        val replyIntent = Intent().apply {
            if (target != -1)
                putExtra("target", target)
        }
        Toast.makeText(
            applicationContext,
            "Authentication succeeded!", Toast.LENGTH_SHORT
        )
            .show()

        setResult(Activity.RESULT_OK, replyIntent)
        finish()
    }


}
