package org.nimjet.psi

import com.intellij.lang.refactoring.RefactoringSupportProvider
import com.intellij.psi.PsiElement

class NimRefactoringSupportProvider: RefactoringSupportProvider() {
	override fun isInplaceRenameAvailable(element: PsiElement, context: PsiElement?): Boolean {
		return true
	}

	override fun isMemberInplaceRenameAvailable(element: PsiElement, context: PsiElement?): Boolean {
		return true
	}
}
