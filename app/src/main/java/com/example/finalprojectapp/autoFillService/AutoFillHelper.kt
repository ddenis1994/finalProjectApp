package com.example.finalprojectapp.autoFillService

import android.view.View


object AutofillHelper {

    fun isValidHint(hint: String): Boolean {
        when (hint) {
            View.AUTOFILL_HINT_CREDIT_CARD_EXPIRATION_DATE,
            View.AUTOFILL_HINT_CREDIT_CARD_EXPIRATION_DAY,
            View.AUTOFILL_HINT_CREDIT_CARD_EXPIRATION_MONTH,
            View.AUTOFILL_HINT_CREDIT_CARD_EXPIRATION_YEAR,
            View.AUTOFILL_HINT_CREDIT_CARD_NUMBER,
            View.AUTOFILL_HINT_CREDIT_CARD_SECURITY_CODE,
            View.AUTOFILL_HINT_EMAIL_ADDRESS,
            View.AUTOFILL_HINT_PHONE,
            View.AUTOFILL_HINT_NAME,
            View.AUTOFILL_HINT_PASSWORD,
            View.AUTOFILL_HINT_POSTAL_ADDRESS,
            View.AUTOFILL_HINT_POSTAL_CODE,
            View.AUTOFILL_HINT_USERNAME ->
                return true
            else ->
                return false
        }
    }
}