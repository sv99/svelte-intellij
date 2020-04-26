package dev.blachut.svelte.lang

import com.intellij.lang.javascript.highlighting.IntentionAndInspectionFilter

class SvelteJSIntentionAndInspectionFilter : IntentionAndInspectionFilter() {
    override fun isSupportedInspection(inspectionToolId: String?): Boolean {
        return if (inspectionToolId == "ES6ModulesDependencies") false else super.isSupportedInspection(inspectionToolId)
    }
}
