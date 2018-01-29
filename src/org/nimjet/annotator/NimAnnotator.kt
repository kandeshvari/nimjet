package org.nimjet.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement
import generated.psi.impl.RoutineIdentifierImpl
import org.nimjet.highlighting.NimSyntaxHighlighter

class NimAnnotator : Annotator {
	override fun annotate(psi: PsiElement, holder: AnnotationHolder) {
		if (psi is RoutineIdentifierImpl) {
			println("A: ${psi::class.qualifiedName} ${psi.text} ${psi.textRange}")
			holder.createInfoAnnotation(psi.textRange, null).textAttributes = NimSyntaxHighlighter.FUNCTION_DECLARATION
		}
	}
}
