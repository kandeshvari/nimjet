package org.nimjet.psi.ref

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiReferenceBase
import generated.psi.*
import org.nimjet.psi.NimNamedElement
import com.intellij.util.PlatformIcons
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.codeInsight.lookup.LookupElement


class RoutineReference(element: Identifier) : PsiReferenceBase<Identifier>(element, TextRange.from(0, element.textLength)) {
	override fun getVariants(): Array<Any?> {
		println("getVariants RoutineReference")
		val routines =
			ScopeWalker<ProcDef>(ProcDef::class.java).findInScope(element.parent)

		return routines.map {
			val name = it.name ?: "null"
			LookupElementBuilder.create(name)
				.withIcon(PlatformIcons.FUNCTION_ICON)
				.withTypeText("Proc")
		}.toTypedArray()
	}

	override fun resolve(): NimNamedElement? {
		val name = element.name ?: return null
		val routine =
			ScopeWalker<ProcDef>(ProcDef::class.java).findInScope(element.parent, name)
		return routine?.nameIdentifier
	}
}
