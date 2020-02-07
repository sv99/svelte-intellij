package dev.blachut.svelte.lang.parsing.js

import com.intellij.lang.Language
import com.intellij.lang.javascript.types.JSFileElementType
import com.intellij.psi.PsiElement
import dev.blachut.svelte.lang.SvelteHTMLLanguage

class SvelteJSFileElementType(language: Language) : JSFileElementType(language) {
    override fun getLanguageForParser(psi: PsiElement?): Language {
        return SvelteHTMLLanguage.INSTANCE
    }
}
