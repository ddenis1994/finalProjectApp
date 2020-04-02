package com.example.finalprojectapp.autoFillService

import android.app.assist.AssistStructure

class AutoFillNodeData(node: AssistStructure.ViewNode) {

    var textValue:String?=null

    var dataValue:Long?=null
    var id:String?=null

    var autofillHints = node.autofillHints?.filter(AutofillHelper::isValidHint)?.toTypedArray()

    init {
        node.autofillValue?.let {
            if (it.isList) {
                val index = it.listValue
                node.autofillOptions?.let { autofillOptions ->
                    if (autofillOptions.size > index) {
                        textValue = autofillOptions[index].toString()
                    }
                }
            } else if (it.isDate) {
                dataValue = it.dateValue
            } else if (it.isText) {

                textValue = it.textValue.toString()
            } else {
            }
        }
    }

}
