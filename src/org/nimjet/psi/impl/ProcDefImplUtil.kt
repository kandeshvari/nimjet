package org.nimjet.psi.impl

import generated.psi.ProcDef
import org.nimjet.psi.NimNamedElement

object ProcDefImplUtil {
	@JvmStatic
	fun getName(e: ProcDef): String? = e.routineIdentifier?.identifier?.name

	@JvmStatic
	fun getNameIdentifier(e: ProcDef): NimNamedElement? = e.routineIdentifier?.identifier

	@JvmStatic
	fun setName(e: ProcDef, newName: String): NimNamedElement {
			println("SET NAME: $newName")
//			val keyNode = e.node.findChildByType(ElementTypes.IDENT)
//			if (keyNode != null) {
//
//				val property = NimElementFactory.createProperty(e.project, newName)
//				val newKeyNode = property.firstChild.node
//				e.node.replaceChild(keyNode, newKeyNode)
//			}
			return getNameIdentifier(e) ?: e
	}
}
