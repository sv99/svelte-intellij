package dev.blachut.svelte.lang.refs

import com.intellij.lang.javascript.psi.JSReferenceExpression
import com.intellij.patterns.ElementPattern
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.*
import com.intellij.psi.filters.ElementFilter
import com.intellij.psi.filters.position.FilterPattern
import com.intellij.util.ProcessingContext

class SvelteDollarReferenceContributor : PsiReferenceContributor() {
    companion object {
        private val REFERENCE_WITH_DOLLAR = createReferenceWithDollar()

        private fun createReferenceWithDollar(): ElementPattern<out PsiElement> {
            return PlatformPatterns.psiElement(JSReferenceExpression::class.java)
                .and(FilterPattern(object : ElementFilter {
                    override fun isAcceptable(element: Any?, context: PsiElement?): Boolean {
                        if (element !is PsiElement) return false
//                        val function = PsiTreeUtil.getParentOfType(element, JSFunction::class.java) ?: return false
//                        if (element !is JSReferenceExpression || element.qualifier !is JSThisExpression) return false
                        if (element !is JSReferenceExpression) return false

                        return element.name?.startsWith('$') ?: false
                    }

                    override fun isClassAcceptable(hintClass: Class<*>?): Boolean {
                        return true
                    }
                }))
        }
    }

    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(REFERENCE_WITH_DOLLAR, SvelteReferenceWithDollarProvider())
    }
}


private class SvelteReferenceWithDollarProvider : PsiReferenceProvider() {
    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
//        if (element is JSReferenceExpressionImpl) { }
        return emptyArray()
    }
}
