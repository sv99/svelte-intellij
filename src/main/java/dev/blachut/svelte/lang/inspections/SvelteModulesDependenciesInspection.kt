package dev.blachut.svelte.lang.inspections

import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.lang.javascript.inspections.JSInspection
import com.intellij.psi.PsiElementVisitor

class SvelteModulesDependenciesInspection : JSInspection() {
    override fun createVisitor(holder: ProblemsHolder, session: LocalInspectionToolSession): PsiElementVisitor {
        return SvelteModulesDependenciesElementVisitor(holder)
    }
}
