package org.nimjet.psi.impl

import com.intellij.openapi.util.Condition
import com.intellij.psi.PsiReference
import com.intellij.psi.util.PsiTreeUtil
import generated.psi.*
import org.nimjet.psi.NimNamedElement
import org.nimjet.psi.ref.IdentifierReference
import org.nimjet.psi.ref.RoutineReference

object IdentifierImplUtil {
	@JvmStatic
	fun getName(e: Identifier): String = e.text

	@JvmStatic
	fun getNameIdentifier(e: Identifier): NimNamedElement = e

	@JvmStatic
	fun setName(e: Identifier, newName: String): NimNamedElement {
		println("Identifier SET NAME: $newName")
//			val keyNode = e.node.findChildByType(ElementTypes.IDENT)
//			if (keyNode != null) {
//
//				val property = NimElementFactory.createProperty(e.project, newName)
//				val newKeyNode = property.firstChild.node
//				e.node.replaceChild(keyNode, newKeyNode)
//			}
		return getNameIdentifier(e)
	}

	@JvmStatic
	fun getReference(element: Identifier): PsiReference? {
//		println("getReference: [${element.text}] [${element.parent.node.elementType}]")

		if (element.parent is RoutineIdentifier ) return RoutineReference(element)

		val x = PsiTreeUtil.findFirstParent(element, Condition { it is CommandExpr || it is CallExpr })

		if (x != null) {
//				println("Found call/command expression")
			return RoutineReference(element)
		}

		//		return new PsiReferenceBase.createSelfReference(element, element);
		return IdentifierReference(element)
//		return null
	}


}
