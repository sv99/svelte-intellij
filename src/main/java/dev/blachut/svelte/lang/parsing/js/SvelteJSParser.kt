package dev.blachut.svelte.lang.parsing.js

import com.intellij.lang.PsiBuilder
import com.intellij.lang.WhitespacesBinders
import com.intellij.lang.ecmascript6.parsing.ES6ExpressionParser
import com.intellij.lang.ecmascript6.parsing.ES6FunctionParser
import com.intellij.lang.ecmascript6.parsing.ES6Parser
import com.intellij.lang.ecmascript6.parsing.ES6StatementParser
import com.intellij.lang.javascript.JSElementTypes
import com.intellij.lang.javascript.JSStubElementTypes
import com.intellij.lang.javascript.JSTokenTypes
import com.intellij.lang.javascript.parsing.JSPsiTypeParser
import com.intellij.lang.javascript.parsing.JavaScriptParserBase
import com.intellij.psi.tree.IElementType

class SvelteJSParser(builder: PsiBuilder) : ES6Parser<ES6ExpressionParser<*>, ES6StatementParser<*>,
    ES6FunctionParser<*>, JSPsiTypeParser<*>>(builder) {
    init {
        myStatementParser = object : ES6StatementParser<SvelteJSParser>(this) {
            override fun getVariableElementType(): IElementType {
                // TODO Try to crate lazy element that splits variable and $ prefix
                return super.getVariableElementType()
            }

            override fun parseDialectSpecificSourceElements(marker: PsiBuilder.Marker): Boolean {
                return tryParseReactiveDeclaration(marker) || super.parseDialectSpecificSourceElements(marker)
            }

            private fun tryParseReactiveDeclaration(marker: PsiBuilder.Marker): Boolean {
                val dollarIdentifier = builder.tokenType == JSTokenTypes.IDENTIFIER && builder.tokenText == "$"
                val reactiveLabel = dollarIdentifier && builder.lookAhead(1) === JSTokenTypes.COLON
                // Treat reactive statements as normal labelled statements
                val reactiveDeclaration = reactiveLabel && isIdentifierToken(builder.lookAhead(2))
                if (!reactiveDeclaration) return false

                if (isAfterModifierKeyword()) {
                    builder.error("reactive declarations can't be exported")
                }

                builder.advanceLexer()
                builder.advanceLexer()

                val varModifierMarker = builder.mark()
                varModifierMarker.collapse(JSTokenTypes.LET_KEYWORD)

                parseVarDeclaration(false)
                forceCheckForSemicolon()

                marker.done(JSStubElementTypes.VAR_STATEMENT)
                marker.setCustomEdgeTokenBinders(JavaScriptParserBase.INCLUDE_DOC_COMMENT_AT_LEFT, WhitespacesBinders.DEFAULT_RIGHT_BINDER)
                return true
            }

            private fun isAfterModifierKeyword(): Boolean {
                val doneMarker = builder.latestDoneMarker ?: return false
                return doneMarker.tokenType == JSElementTypes.ATTRIBUTE_LIST && doneMarker.textLength > 0
            }
        }
    }
}
