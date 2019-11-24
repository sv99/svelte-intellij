package dev.blachut.svelte.lang.parsing.js

import com.intellij.lang.PsiBuilder
import com.intellij.lang.impl.DelegateMarker
import com.intellij.psi.tree.IElementType

class SvelteJSPsiBuilderMarker(delegate: PsiBuilder.Marker) : DelegateMarker(delegate) {
    private var doneIgnored = false

    override fun done(type: IElementType) {
        if (!doneIgnored) {
            super.done(type)
        }
    }

    fun ignoreDone() {
        doneIgnored = true
    }
}
