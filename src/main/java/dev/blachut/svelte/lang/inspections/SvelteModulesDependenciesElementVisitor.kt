package dev.blachut.svelte.lang.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.lang.javascript.JavaScriptBundle
import com.intellij.lang.javascript.modules.JSBaseModulesDependenciesElementVisitor
import com.intellij.lang.javascript.modules.JsModulesSuggester
import com.intellij.lang.javascript.modules.ModuleReferenceInfo
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveResult

internal class SvelteModulesDependenciesElementVisitor(holder: ProblemsHolder) : JSBaseModulesDependenciesElementVisitor(holder) {
    override fun getInspectionText(): String {
        return JavaScriptBundle.message("js.inspection.es6.modules.dependencies.family.name")
    }

    override fun createSuggester(node: PsiElement, info: ModuleReferenceInfo, resolveResults: Array<ResolveResult>): JsModulesSuggester? {
        return SvelteModulesSuggester(info, node)
    }
}
