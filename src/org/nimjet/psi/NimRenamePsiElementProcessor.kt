package org.nimjet.psi

import com.intellij.psi.PsiElement
import com.intellij.refactoring.rename.RenamePsiElementProcessor


class NimRenamePsiElementProcessor : RenamePsiElementProcessor() {
	override fun canProcessElement(element: PsiElement): Boolean {
		// TODO: filter elements that can be renamed
		return true
	}

	override fun prepareRenaming(element: PsiElement, newName: String, allRenames: MutableMap<PsiElement, String>) {
	}
}
