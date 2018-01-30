package org.nimjet.psi.ref

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.ResolveResult
import com.intellij.psi.PsiElementResolveResult
import org.nimjet.psi.NimUtil
import java.util.ArrayList
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.openapi.util.TextRange
import org.nimjet.NimIcons


class NimReference(element: PsiElement, textRange: TextRange): PsiReferenceBase<PsiElement>(element, textRange), PsiPolyVariantReference {
	private val key: String = ""
	override fun resolve(): PsiElement? {
		val resolveResults = multiResolve(false)
		return if (resolveResults.size == 1) resolveResults[0].element else null
	}

	override fun getVariants(): Array<Any> {
		val project = myElement.project
		val properties = NimUtil.findProperties(project)
		val variants = ArrayList<LookupElement>()
		for (property in properties) {
			val name = property.name
			if (name != null && name.isNotEmpty()) {
				variants.add(LookupElementBuilder.create(property).withIcon(NimIcons.FILE).withTypeText(property.containingFile.name)
				)
			}
		}
		return variants.toTypedArray()
	}

	override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
		val project = myElement.project
		val properties = NimUtil.findProperties(project, key)
		val results = properties.map { PsiElementResolveResult(it) }
		return results.toTypedArray()
	}

}
