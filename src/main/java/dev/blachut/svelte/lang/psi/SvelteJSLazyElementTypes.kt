package dev.blachut.svelte.lang.psi

import com.intellij.lang.PsiBuilder
import com.intellij.lang.javascript.JSElementTypes
import com.intellij.lang.javascript.JSTokenTypes
import com.intellij.lang.javascript.parsing.JavaScriptParser

object SvelteJSLazyElementTypes {
    val PARAMETER = object : SvelteJSLazyElementType("PARAMETER") {
        override val noTokensErrorMessage = "parameter expected"

        override fun parseTokens(builder: PsiBuilder, parser: JavaScriptParser<*, *, *, *>) {
            parser.expressionParser.parseDestructuringElement(SvelteJSElementTypes.PARAMETER, false, false)
        }
    }

    // TODO Break into elements that allow and disallow comma expressions
    val EXPRESSION = object : SvelteJSLazyElementType("EXPRESSION") {
        override val noTokensErrorMessage = "expression expected"

        override fun parseTokens(builder: PsiBuilder, parser: JavaScriptParser<*, *, *, *>) {
            parser.expressionParser.parseExpression()
        }
    }

    val SPREAD_OR_SHORTHAND = object : SvelteJSLazyElementType("SPREAD_OR_SHORTHAND") {
        override val noTokensErrorMessage = "shorthand attribute or spread expression expected"

        override fun parseTokens(builder: PsiBuilder, parser: JavaScriptParser<*, *, *, *>) {
            if (builder.tokenType === JSTokenTypes.DOT_DOT_DOT) {
                val marker = builder.mark()
                builder.advanceLexer()
                parser.expressionParser.parseAssignmentExpression(false)
                marker.done(JSElementTypes.SPREAD_EXPRESSION)
            } else {
                parser.expressionParser.parseAssignmentExpression(false)
            }
        }
    }
}
