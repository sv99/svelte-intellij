package dev.blachut.svelte.lang.psi

import com.intellij.lang.ASTNode
import com.intellij.lang.PsiBuilder
import com.intellij.lang.PsiBuilderFactory
import com.intellij.lang.javascript.JSElementTypes
import com.intellij.lang.javascript.JSTokenTypes
import com.intellij.lang.javascript.parsing.JavaScriptParser
import com.intellij.psi.PsiElement
import dev.blachut.svelte.lang.SvelteJSLanguage

object SvelteJSLazyElementTypes {
    val SCRIPT = object : SvelteJSLazyElementType("PARAMETER") {
        override val noTokensErrorMessage = "parameter expected"

        override fun doParseContents(chameleon: ASTNode, psi: PsiElement): ASTNode {
            val project = psi.project
            val builder = PsiBuilderFactory.getInstance().createBuilder(project, chameleon, null, SvelteJSLanguage.INSTANCE, chameleon.chars)
            val parser = createJavaScriptParser(builder)

            parser.parseJS(this)

            return builder.treeBuilt.firstChildNode
        }

        override fun parseTokens(builder: PsiBuilder, parser: JavaScriptParser<*, *, *, *>) {
            parser.parseJS(this)
        }
    }

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
