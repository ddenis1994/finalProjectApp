package com.example.finalprojectapp.ui.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.service.autofill.FillResponse
import android.view.autofill.AutofillManager.EXTRA_AUTHENTICATION_RESULT
import androidx.appcompat.app.AppCompatActivity

class ServiceAuthActivity : AppCompatActivity() {
    private var structure: FillResponse?=null
    private val requestCodeForDelete:Int=3001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        structure = intent.getParcelableExtra("response")
        val intent = Intent(this, AppAuthActivity::class.java)
        startActivityForResult(intent,requestCodeForDelete)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val replyIntent = Intent().apply {
                putExtra(EXTRA_AUTHENTICATION_RESULT, structure)
            }
            setResult(RESULT_OK, replyIntent)
            finish()

        }
    }


}
