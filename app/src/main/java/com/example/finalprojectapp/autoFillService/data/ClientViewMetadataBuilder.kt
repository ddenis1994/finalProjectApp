package com.example.finalprojectapp.autoFillService.data

import android.app.assist.AssistStructure.ViewNode
import com.example.finalprojectapp.autoFillService.AutoFillNodeData
import com.example.finalprojectapp.autoFillService.AutofillFieldMetadata
import com.example.finalprojectapp.autoFillService.ClientParser

class ClientViewMetadataBuilder(private val mClientParser: ClientParser) {

    fun buildClientViewMetadata(): MutableList<AutofillFieldMetadata> {
        val allHints = mutableListOf<AutofillFieldMetadata>()
        mClientParser.parse { node:ViewNode -> parseNodeForFill(node,allHints)}
        return allHints
    }

    fun buildClientSaveMetadata(): MutableList<AutoFillNodeData> {
        val allHints = mutableListOf<AutoFillNodeData>()
        mClientParser.parse { node:ViewNode -> parseNodeForSave(node,allHints)}
        return allHints
    }

    private fun parseNodeForSave(
        root: ViewNode,
        list: MutableList<AutoFillNodeData>
    ) {
        val hints = root.autofillHints
        if (hints != null) {
            list.add(AutoFillNodeData(root))
        }
    }


    private fun parseNodeForFill(
        root: ViewNode,
        list: MutableList<AutofillFieldMetadata>
    ) {
        val hints = root.autofillHints
        if (hints != null) {
            list.add(AutofillFieldMetadata(root))
        }
    }



}


