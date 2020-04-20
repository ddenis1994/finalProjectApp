package com.example.finalprojectapp.autoFillService

import android.app.assist.AssistStructure
import android.app.assist.AssistStructure.ViewNode
import com.google.common.base.Preconditions
import com.google.common.collect.ImmutableList

class ClientParser(structures:List<AssistStructure>) {
    private val  mStructures: List<AssistStructure>
    init {
        Preconditions.checkNotNull(structures)
        this.mStructures = structures
    }

    constructor(structure:AssistStructure ) : this(ImmutableList.of(structure))

    fun parse(processor: (ViewNode) -> Unit) {
        for (structure in mStructures) {
            val nodes = structure.windowNodeCount
            for (i in 0 until nodes) {
                val viewNode = structure.getWindowNodeAt(i).rootViewNode
                traverseRoot(viewNode, processor)
            }
        }
    }
    private fun traverseRoot(viewNode: ViewNode, processor: (ViewNode) -> Unit) {
        processor(viewNode)
        val childrenSize = viewNode.childCount
        if (childrenSize > 0) {
            for (i in 0 until childrenSize) {
                traverseRoot(viewNode.getChildAt(i), processor)
            }
        }
    }

}