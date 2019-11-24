package dev.blachut.svelte.lang.parsing.js

import com.intellij.lang.PsiBuilder
import com.intellij.lang.WhitespacesBinders
import com.intellij.lang.ecmascript6.parsing.ES6ExpressionParser
import com.intellij.lang.ecmascript6.parsing.ES6FunctionParser
import com.intellij.lang.ecmascript6.parsing.ES6Parser
import com.intellij.lang.ecmascript6.parsing.ES6StatementParser
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
                val firstTokenType = builder.tokenType
                if (firstTokenType == JSTokenTypes.IDENTIFIER && builder.tokenText == "$" && builder.lookAhead(1) === JSTokenTypes.COLON) {
                    builder.advanceLexer()
                    builder.advanceLexer()

                    val varModifierMarker = builder.mark()
                    varModifierMarker.done(JSTokenTypes.CONST_KEYWORD)

                    parseVarDeclaration(false)
                    forceCheckForSemicolon()

                    marker.done(JSStubElementTypes.VAR_STATEMENT)
                    marker.setCustomEdgeTokenBinders(JavaScriptParserBase.INCLUDE_DOC_COMMENT_AT_LEFT, WhitespacesBinders.DEFAULT_RIGHT_BINDER)
                    return true
                }

                return super.parseDialectSpecificSourceElements(marker)
            }
        }
    }
}
