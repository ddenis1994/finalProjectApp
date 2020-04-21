/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.finalprojectapp.autoFillService

import android.app.assist.AssistStructure.ViewNode;
import android.service.autofill.SaveInfo
import android.view.View
import android.view.autofill.AutofillId


/**
 * A stripped down version of a [ViewNode] that contains only autofill-relevant metadata. It also
 * contains a `saveType` flag that is calculated based on the [ViewNode]'s autofill hints.
 */
class AutofillFieldMetadata(view: ViewNode) {
    var saveType = 0
        private set

    val autofillHints = view.autofillHints!!.filter(AutofillHelper::isValidHint).toTypedArray()
    val autofillId: AutofillId = view.autofillId!!
    val autofillType: Int = view.autofillType
    val isFocused: Boolean = view.isFocused

    init {
        updateSaveTypeFromHints()
    }

    private fun updateSaveTypeFromHints() {
        saveType = 0
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
