package com.example.finalprojectapp.autoFillService

import android.app.assist.AssistStructure
import android.service.autofill.SaveInfo
import android.view.View
import android.view.autofill.AutofillId

// this class holds the relevent data from viewNode

class AutoFillNodeMetadate (val view: AssistStructure.ViewNode){
    var saveType = 0
        private set

    val autofillHints = view.autofillHints?.filter(AutofillHelper::isValidHint)?.toTypedArray()
    val autofillId: AutofillId? = view.autofillId
    val autofillType: Int = view.autofillType
    val autofillOptions: Array<CharSequence>? = view.autofillOptions
    val isFocused: Boolean = view.isFocused

    init {
        updateSaveTypeFromHints()
    }



    private fun updateSaveTypeFromHints() {
        saveType = 0
        if (autofillHints != null) {
            for (hint in autofillHints) {
                when (hint) {
                    View.AUTOFILL_HINT_CREDIT_CARD_EXPIRATION_DATE,
                    View.AUTOFILL_HINT_CREDIT_CARD_EXPIRATION_DAY,
                    View.AUTOFILL_HINT_CREDIT_CARD_EXPIRATION_MONTH,
                    View.AUTOFILL_HINT_CREDIT_CARD_EXPIRATION_YEAR,
                    View.AUTOFILL_HINT_CREDIT_CARD_NUMBER,
                    View.AUTOFILL_HINT_CREDIT_CARD_SECURITY_CODE -> {
                        saveType = saveType or SaveInfo.SAVE_DATA_TYPE_CREDIT_CARD
                    }
                    View.AUTOFILL_HINT_EMAIL_ADDRESS -> {
                        saveType = saveType or SaveInfo.SAVE_DATA_TYPE_EMAIL_ADDRESS
                    }
                    View.AUTOFILL_HINT_PHONE, View.AUTOFILL_HINT_NAME -> {
                        saveType = saveType or SaveInfo.SAVE_DATA_TYPE_GENERIC
                    }
                    View.AUTOFILL_HINT_PASSWORD -> {
                        saveType = saveType or SaveInfo.SAVE_DATA_TYPE_PASSWORD
                        saveType = saveType and SaveInfo.SAVE_DATA_TYPE_EMAIL_ADDRESS.inv()
                        saveType = saveType and SaveInfo.SAVE_DATA_TYPE_USERNAME.inv()
                    }
                    View.AUTOFILL_HINT_POSTAL_ADDRESS,
                    View.AUTOFILL_HINT_POSTAL_CODE -> {
                        saveType = saveType or SaveInfo.SAVE_DATA_TYPE_ADDRESS
                    }
                    View.AUTOFILL_HINT_USERNAME -> {
                        saveType = saveType or SaveInfo.SAVE_DATA_TYPE_USERNAME
                    }
                }
            }
        }
    }

}