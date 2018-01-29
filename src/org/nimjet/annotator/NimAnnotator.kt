package org.nimjet.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement
import generated.psi.impl.*
import org.nimjet.highlighting.NimSyntaxHighlighter
import org.nimjet.psi.ElementTypes.*

class NimAnnotator : Annotator {
	var BUILTIN_TYPES_KEYWORDS = listOf("string", "int", "untyped")
	override fun annotate(psi: PsiElement, holder: AnnotationHolder) {
//		println("A: ${psi::class.qualifiedName} [${psi.text}] ${psi.textRange}")
		when (psi) {
			is RoutineIdentifierImpl -> {
				/* routine name */
				holder.createInfoAnnotation(psi.textRange, null).textAttributes = NimSyntaxHighlighter.FUNCTION_DECLARATION

			}
			is IdentifierImpl -> {
				/* Type name in definition */
				if (psi.parent is TypeDefImpl)
					holder.createInfoAnnotation(psi.textRange, null).textAttributes = NimSyntaxHighlighter.TYPE_DEFINITION

			}
			is IdentifierExprImpl -> {
				/* type name in routine declaration list */
				if (psi.context is TypeDescImpl || psi.context is VarTypeExprImpl) {
					if (psi.text in BUILTIN_TYPES_KEYWORDS)
						/* type is builtin (int, string, untyped, ...)*/
						holder.createInfoAnnotation(psi.textRange, null).textAttributes = NimSyntaxHighlighter.BUILTIN_TYPE
					else
						holder.createInfoAnnotation(psi.textRange, null).textAttributes = NimSyntaxHighlighter.TYPE
				} else if (psi.text == "result")
						holder.createInfoAnnotation(psi.textRange, null).textAttributes = NimSyntaxHighlighter.RESULT_PARAM

			}
			is IdentPragmaPairImpl -> {
				if (psi.context is IdentifierDefsImpl)
					holder.createInfoAnnotation(psi.textRange, null).textAttributes = NimSyntaxHighlighter.FUNCTION_PARAMETER
			}
		}

		when (psi.node.elementType) {
			OPERATOR -> when(psi.context) {
				 is ProcDefImpl -> holder.createInfoAnnotation(psi.textRange, null).textAttributes = NimSyntaxHighlighter.PUBLIC_OPERATOR
			}
		}
	}
}
