package dev.blachut.svelte.lang.psi

import com.intellij.lang.javascript.psi.JSFile
import com.intellij.lang.javascript.psi.impl.JSFileImpl
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.psi.impl.source.tree.CompositeElement
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi.search.PsiElementProcessor
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.XmlDocument
import com.intellij.psi.xml.XmlElementType
import com.intellij.psi.xml.XmlTag
import com.intellij.xml.util.HtmlUtil
import dev.blachut.svelte.lang.SvelteJSLanguage
import dev.blachut.svelte.lang.getJsEmbeddedContent

class SvelteHtmlFile(viewProvider: FileViewProvider) : JSFileImpl(viewProvider, SvelteJSLanguage.INSTANCE) {
    val moduleScript get() = document?.children?.find { it is XmlTag && HtmlUtil.isScriptTag(it) && it.getAttributeValue("context") == "module" } as XmlTag?
    // By convention instanceScript is placed after module script
    // so it makes sense to resolve last script in case of ambiguity from missing context attribute
    // ambiguous scripts should then be highlighted by appropriate inspection
    val instanceScript get() = document?.children?.findLast { it is XmlTag && HtmlUtil.isScriptTag(it) && it.getAttributeValue("context") == null } as XmlTag?


    val document: XmlDocument?
        get() {
            val treeElement: CompositeElement = calcTreeElement()
            val node = treeElement.findChildByType(XmlElementType.HTML_DOCUMENT)
            return if (node != null) node.psi as XmlDocument else null
        }

    fun processElements(processor: PsiElementProcessor<*>?, place: PsiElement?): Boolean {
        return document == null || document!!.processElements(processor, place)
    }

    override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement): Boolean {
        document ?: return true

        val parentScript = findAncestorScript(place)
        if (parentScript != null && parentScript.getAttributeValue("context") == "module") {
            // place is inside module script, nothing more to process
            return true
        } else if (parentScript != null) {
            // place is inside instance script, process module script if available
            return processScriptDeclarations(processor, state, lastParent, place, moduleScript)
        } else {
            // place is inside template expression, process instance and then module script if available
            return processScriptDeclarations(processor, state, lastParent, place, instanceScript)
                && processScriptDeclarations(processor, state, lastParent, place, moduleScript)
        }
    }

    private fun processScriptDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement, script: PsiElement?): Boolean {
        return getJsEmbeddedContent(script)?.processDeclarations(processor, state, lastParent, place) ?: true
    }

    override fun hasES6Syntax(): Boolean = true
    override fun hasFlowFlag(): Boolean = false
    override fun getModuleStatus(): JSFile.ModuleStatus {
        return JSFile.ModuleStatus.ES6
    }
}

private fun findAncestorScript(place: PsiElement): XmlTag? {
    val parentScript = PsiTreeUtil.findFirstContext(place, false) {
        it is XmlTag && HtmlUtil.isScriptTag(it)
    }
    return parentScript as XmlTag?
}
