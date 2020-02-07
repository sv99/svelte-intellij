package dev.blachut.svelte.lang.parsing.js

import com.intellij.lang.PsiParser
import com.intellij.lang.javascript.dialects.ECMA6ParserDefinition
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IFileElementType
import dev.blachut.svelte.lang.SvelteJSLanguage
import dev.blachut.svelte.lang.psi.SvelteHtmlFile

class SvelteJSParserDefinition : ECMA6ParserDefinition() {
    override fun getFileNodeType(): IFileElementType {
        return FILE
    }

    override fun createFile(viewProvider: FileViewProvider): PsiFile {
        return SvelteHtmlFile(viewProvider)
    }

    override fun createParser(project: Project?): PsiParser {
        return PsiParser { root, builder ->
            SvelteJSLanguage.INSTANCE.createParser(builder).parseJS(root)
            builder.treeBuilt
        }
    }

    companion object {
        val FILE: IFileElementType = SvelteJSFileElementType(SvelteJSLanguage.INSTANCE)
    }
}
