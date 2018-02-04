package org.nimjet.psi.ref

import com.intellij.psi.PsiElement
import org.nimjet.psi.NimNamedElement

object RoutineHolder {
	var data : HashMap<String, NimNamedElement> = HashMap<String, NimNamedElement>()

	fun getByName(name: String?): NimNamedElement? {
		if (name == null) return null
		println("RoutineHolder: get [$name] [${data.keys}]")
		return data[name]
	}

	fun setByName(name: String, element: NimNamedElement) {
		println("RoutineHolder: set [$name]")
		data[name] = element
	}

	fun deleteByName(name: String) {
		println("RoutineHolder: remove [$name]")
		data.remove(name)
	}
}


