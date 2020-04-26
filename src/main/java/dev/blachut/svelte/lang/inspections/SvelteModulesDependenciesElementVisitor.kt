package dev.blachut.svelte.lang.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.lang.javascript.JavaScriptBundle
import com.intellij.lang.javascript.modules.*
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveResult

internal class SvelteModulesDependenciesElementVisitor(holder: ProblemsHolder) : JSBaseModulesDependenciesElementVisitor(holder) {
    override fun getInspectionText(): String {
        return JavaScriptBundle.message("js.inspection.es6.modules.dependencies.family.name")
    }

    override fun createSuggester(node: PsiElement, info: ModuleReferenceInfo, resolveResults: Array<ResolveResult>): JsModulesSuggester? {
        return if (!NodeModuleUtil.isWrappedInAmdDefinition(node)) ES6ModulesSuggester(info, node) else null
    }
}
