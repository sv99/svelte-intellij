package dev.blachut.svelte.lang.inspections

import com.intellij.lang.ecmascript6.psi.impl.ES6ImportPsiUtil.CreateImportExportInfo
import com.intellij.lang.ecmascript6.psi.impl.ES6ImportPsiUtil.ImportExportType
import com.intellij.lang.javascript.modules.ImportES6ModuleFix
import com.intellij.lang.javascript.modules.JSModuleFixDescriptor
import com.intellij.lang.javascript.modules.JSPlaceTail
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement

class SvelteImportES6ModuleFix(node: PsiElement, descriptor: JSModuleFixDescriptor, tail: JSPlaceTail?, quoteString: String?, needHint: Boolean) : ImportES6ModuleFix(node, descriptor, tail, quoteString, needHint) {
    override fun executeImpl(element: PsiElement, editor: Editor?, scope: PsiElement) {
        SvelteAddImportExecutor(editor, element).createImportOrUseExisting(importData, null, myQuotes + this.path + myQuotes)
        replaceReferences(element, editor)
    }

    private val importData: ImportData
        get() {
            val importedName = myFixDescriptor.importedName
            val type = myFixDescriptor.importType
            val exportedName = myFixDescriptor.exportedName
            return if (exportedName != null) {
                val typeToUse = type ?: ImportExportType.SPECIFIER
                if (exportedName == importedName) ImportData(this, importedName, typeToUse) else ImportData(this, exportedName, importedName, typeToUse)
            } else if (type != null) {
                ImportData(this, importedName, type)
            } else if (myTail != null) {
                val tail = myTail.strings[0]
                ImportData(this, tail ?: importedName, ImportExportType.SPECIFIER)
            } else {
                ImportData(this, importedName, ImportExportType.IMPORT_BINDING_ALL)
            }
        }

    private class ImportData : CreateImportExportInfo {
        internal constructor(fix: ImportES6ModuleFix?, exportedName: String?, importedName: String, importType: ImportExportType?) : super(exportedName, importedName, importType!!, true, false)
        internal constructor(fix: ImportES6ModuleFix, importedName: String, importType: ImportExportType?) : super(null, importedName, importType!!, true, false)
    }



}
