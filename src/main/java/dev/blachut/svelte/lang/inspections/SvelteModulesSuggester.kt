package dev.blachut.svelte.lang.inspections

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.lang.javascript.formatter.JSCodeStyleSettings
import com.intellij.lang.javascript.modules.ES6ModulesSuggester
import com.intellij.lang.javascript.modules.JSModuleFixDescriptor
import com.intellij.lang.javascript.modules.JSPlaceTail
import com.intellij.lang.javascript.modules.ModuleReferenceInfo
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveResult
import com.intellij.psi.SmartPointerManager
import com.intellij.util.containers.ContainerUtil
import com.intellij.util.containers.MultiMap
import java.util.*

class SvelteModulesSuggester(info: ModuleReferenceInfo, node: PsiElement) : ES6ModulesSuggester(info, node) {
    override fun findFixes(resolveResults: Array<ResolveResult>): MultiMap<PsiElement, LocalQuickFix> {
        var descriptors = this.find(resolveResults, false)
        if (descriptors.isEmpty()) {
            return MultiMap.empty()
        } else {
            descriptors = ContainerUtil.filter(descriptors) { descriptorx: JSModuleFixDescriptor ->
                val fromPath = descriptorx.fromPath
                val idx = fromPath.indexOf("node_modules")
                idx < 0 || fromPath.indexOf("test", idx) <= 0 && fromPath.indexOf("examples", idx) <= 0
            }
            val quoteString = JSCodeStyleSettings.getQuote(myNode)
            val list: MutableList<LocalQuickFix?> = ArrayList()
            val result = MultiMap.createLinked<PsiElement, LocalQuickFix>()
            val size = descriptors.size
            val var7: Iterator<*> = descriptors.iterator()
            while (var7.hasNext()) {
                val descriptor = var7.next() as JSModuleFixDescriptor
                val hint = size == 1 && myModuleReferenceInfo.needHint()
                list.add(SvelteImportES6ModuleFix(myNode, descriptor, null as JSPlaceTail?, quoteString, hint))
            }
            result.putValues(myNode, list)
            val parent = myModuleReferenceInfo.parentRef
            if (parent != null) {
                val project = myNode.project
                val secondWordList: MutableList<LocalQuickFix?> = ArrayList(list)
                val var10: Iterator<*> = descriptors.iterator()
                while (var10.hasNext()) {
                    val descriptor = var10.next() as JSModuleFixDescriptor
                    val tail = JSPlaceTail(SmartPointerManager.getInstance(project).createSmartPsiElementPointer(parent), arrayOf(myModuleReferenceInfo.parentName))
                    secondWordList.add(SvelteImportES6ModuleFix(parent, descriptor, tail, quoteString, false))
                }
                result.putValues(parent, secondWordList)
            }
            return result
        }
    }
}
