package org.nimjet.psi

import com.intellij.psi.tree.IElementType
import org.nimjet.NimLanguage

class NimTokenType(debugName: String) : IElementType(debugName, NimLanguage) {
	override fun toString(): String = "NimTokenType." + super.toString()
}
