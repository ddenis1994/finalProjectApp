package com.example.finalprojectapp.autoFillService

import android.app.assist.AssistStructure
import android.util.Log

import com.example.finalprojectapp.autoFillService.CommonUtil.TAG
import com.example.finalprojectapp.autoFillService.model.FilledAutofillFieldCollection
import com.example.finalprojectapp.autoFillServicemodel.FilledAutofillField

open class StructureParser(private val autofillStructure: AssistStructure) {
    val autofillFields = AutofillFieldMetadataCollection()
    var filledAutofillFieldCollection: FilledAutofillFieldCollection = FilledAutofillFieldCollection()
        private set

    fun parseForFill() {
        parse(true)
    }

    fun parseForSave() {
        parse(false)
    }

    /**
     * Traverse AssistStructure and add ViewNode metadata to a flat list.
     */
    private fun parse(forFill: Boolean) {
        Log.d(TAG, "Parsing structure for " + autofillStructure.activityComponent)
        val nodes = autofillStructure.windowNodeCount
        filledAutofillFieldCollection = FilledAutofillFieldCollection()
        for (i in 0 until nodes) {
            parseLocked(forFill, autofillStructure.getWindowNodeAt(i).rootViewNode)
        }
    }

    private fun parseLocked(forFill: Boolean, viewNode: AssistStructure.ViewNode) {
        viewNode.autofillHints?.let { autofillHints ->
            if (autofillHints.isNotEmpty()) {
                if (forFill) {
                    autofillFields.add(AutofillFieldMetadata(viewNode))
                } else {
                    filledAutofillFieldCollection.add(FilledAutofillField(viewNode))
                }
            }
        }
        val childrenSize = viewNode.childCount
        for (i in 0 until childrenSize) {
            parseLocked(forFill, viewNode.getChildAt(i))

        }
    }
}