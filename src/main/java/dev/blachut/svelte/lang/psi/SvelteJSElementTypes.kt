package dev.blachut.svelte.lang.psi

import com.intellij.lang.ASTNode
import com.intellij.lang.javascript.types.JSEmbeddedContentElementType
import com.intellij.lang.javascript.types.JSParameterElementType
import com.intellij.psi.PsiElement
import dev.blachut.svelte.lang.SvelteJSLanguage

object SvelteJSElementTypes {
    val PARAMETER = object : JSParameterElementType("EMBEDDED_PARAMETER") {
        override fun construct(node: ASTNode): PsiElement? {
            return SvelteJSParameter(node)
        }
    }

    val ATTRIBUTE_EXPRESSION = SvelteElementType("ATTRIBUTE_EXPRESSION")

    val EMBEDDED_CONTENT = JSEmbeddedContentElementType(SvelteJSLanguage.INSTANCE, "SVELTE_")
}

