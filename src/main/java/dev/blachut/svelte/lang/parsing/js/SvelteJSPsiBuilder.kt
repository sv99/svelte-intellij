package dev.blachut.svelte.lang.parsing.js

import com.intellij.lang.PsiBuilder
import com.intellij.lang.impl.PsiBuilderAdapter
import com.intellij.lang.javascript.JSTokenTypes
import com.intellij.psi.tree.IElementType

class SvelteJSPsiBuilder(delegate: PsiBuilder) : PsiBuilderAdapter(delegate) {
    private var lastButOneTokenType: IElementType? = null
    private var lastTokenType: IElementType? = null
    private var lastButOneTokenText: String? = null
    private var lastTokenText: String? = null
    var lastMarker: SvelteJSPsiBuilderMarker? = null
        private set

    fun seenSvelteLabelStart(): Boolean {
        return lastButOneTokenType === JSTokenTypes.IDENTIFIER && lastTokenType === JSTokenTypes.COLON && lastButOneTokenText == "$"
    }

    override fun advanceLexer() {
        lastButOneTokenType = lastTokenType
        lastTokenType = tokenType
        lastButOneTokenText = lastTokenText
        lastTokenText = if (tokenType === JSTokenTypes.IDENTIFIER) tokenText else null
        super.advanceLexer()
    }

    override fun mark(): PsiBuilder.Marker {
        val baseMarker = super.mark()
        val marker = SvelteJSPsiBuilderMarker(baseMarker)
        lastMarker = marker
        return marker
    }
}

