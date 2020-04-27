package dev.blachut.svelte.lang.inspections

import com.intellij.lang.ecmascript6.psi.ES6ImportDeclaration
import com.intellij.lang.ecmascript6.psi.ES6ImportExportDeclarationPart
import com.intellij.lang.ecmascript6.psi.impl.ES6CreateImportUtil
import com.intellij.lang.ecmascript6.psi.impl.ES6ImportPsiUtil
import com.intellij.lang.ecmascript6.psi.impl.ES6ImportPsiUtil.CreateImportExportInfo
import com.intellij.lang.javascript.DialectDetector
import com.intellij.lang.javascript.formatter.JSCodeStyleSettings
import com.intellij.lang.javascript.psi.JSEmbeddedContent
import com.intellij.lang.typescript.psi.TypeScriptAutoImportUtil
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.XmlElementFactory
import com.intellij.psi.xml.XmlTag
import dev.blachut.svelte.lang.SvelteHTMLLanguage
import dev.blachut.svelte.lang.parsing.js.SvelteJSScriptContentProvider
import dev.blachut.svelte.lang.psi.SvelteHtmlFile
import dev.blachut.svelte.lang.psi.findAncestorScript

/**
 * Adapted from com.intellij.lang.ecmascript6.actions.ES6AddImportExecutor
 */
class SvelteAddImportExecutor(val editor: Editor?, val place: PsiElement) {
    fun createImportOrUseExisting(info: CreateImportExportInfo, externalModule: PsiElement?, quotedModuleName: String): Boolean {
        val type = info.importType
        val scope = findOrCreateScriptContent()

        return if (tryToUseExistingImport(info, quotedModuleName, externalModule, scope)) {
            true
        } else {
            val importPsi = createTypeScriptOrES6Import(quotedModuleName, info)
            if (importPsi == null) {
                false
            } else {
                if (importPsi is ES6ImportDeclaration && type !== ES6ImportPsiUtil.ImportExportType.BARE) {
                    ES6CreateImportUtil.findPlaceAndInsertES6Import(scope, importPsi, StringUtil.unquoteString(quotedModuleName), editor)
                } else {
                    ES6CreateImportUtil.findPlaceAndInsertAnyImport(scope, importPsi, editor)
                }
                true
            }
        }
    }

    private fun findOrCreateScriptContent(): JSEmbeddedContent {
        val parentScript = findAncestorScript(place)
        if (parentScript != null) {
            // parent module or instance script
            return SvelteJSScriptContentProvider.getJsEmbeddedContent(parentScript)!!
        }

        val currentFile = place.containingFile as SvelteHtmlFile
        val instanceScript = currentFile.instanceScript
        if (instanceScript != null) {
            // TODO empty tag
            return SvelteJSScriptContentProvider.getJsEmbeddedContent(instanceScript)!!
        }

        val elementFactory = XmlElementFactory.getInstance(currentFile.project)
        val emptyInstanceScript = elementFactory.createTagFromText("<script>\n</script>", SvelteHTMLLanguage.INSTANCE)
        val moduleScript = currentFile.moduleScript
        val document = currentFile.document!!

        val script = if (moduleScript != null) {
            document.addAfter(emptyInstanceScript, moduleScript) as XmlTag
        } else {
            document.addBefore(emptyInstanceScript, document.firstChild) as XmlTag
        }

        return SvelteJSScriptContentProvider.getJsEmbeddedContent(script)!!
    }

    private fun tryToUseExistingImport(info: CreateImportExportInfo, quotedModuleOrNamespaceName: String, externalModule: PsiElement?, scope: PsiElement): Boolean {
        val parent: PsiElement = place.parent
        if (parent is ES6ImportExportDeclarationPart) {
            val grandParent = parent.declaration
            if (grandParent is ES6ImportDeclaration && grandParent.getFromClause() == null) {
                ES6CreateImportUtil.insertFromClause(parent, grandParent, quotedModuleOrNamespaceName)
                return true
            }
        }
        val importType = info.importType
        return if (JSCodeStyleSettings.isMergeImports(place) && importType.isES6) {
            val possibleImport = ES6ImportPsiUtil.findExistingES6Import(scope, externalModule, quotedModuleOrNamespaceName, info)
            possibleImport != null && ES6ImportPsiUtil.tryToAddImportToExistingDeclaration(possibleImport, info)
        } else {
            false
        }
    }

    private fun createTypeScriptOrES6Import(externalModuleName: String, info: CreateImportExportInfo): PsiElement? {
        return if (!info.importType.isES6 && !DialectDetector.isJavaScript(place)) {
            TypeScriptAutoImportUtil.createTypeScriptImport(place, info, externalModuleName)
        } else {
            ES6ImportPsiUtil.createImportOrExport(place, info, externalModuleName)
        }
    }
}
