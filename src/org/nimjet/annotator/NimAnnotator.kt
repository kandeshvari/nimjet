package org.nimjet.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.psi.PsiElement
import generated.psi.impl.*
import org.nimjet.formatter.NimBlockUtils
import org.nimjet.highlighting.NimSyntaxHighlighter
import org.nimjet.psi.ElementTypes.*

class NimAnnotator : Annotator {
	var BUILTIN_TYPES_KEYWORDS = hashSetOf<String>("string", "int", "int8", "int16", "int32", "int64", "BiggestInt",
		"float", "float32", "float64", "untyped", "varargs")

	override fun annotate(psi: PsiElement, holder: AnnotationHolder) {
		fun annotateSyntaxElementAs(key: TextAttributesKey) {
			holder.createInfoAnnotation(psi.textRange, null).textAttributes = key
		}

		when (psi) {
			/* routine name */
			is RoutineIdentifierImpl -> annotateSyntaxElementAs(NimSyntaxHighlighter.FUNCTION_DECLARATION)
			is IdentifierImpl ->
				/* Type name in definition */
				if (psi.parent is TypeDefImpl)
					annotateSyntaxElementAs(NimSyntaxHighlighter.TYPE_DEFINITION)
			is IdentifierExprImpl -> {
				/* type name in routine declaration list */
				if (NimBlockUtils.ancestorOf(TYPE_DESC, psi.node)) {
					if (psi.text in BUILTIN_TYPES_KEYWORDS)
						/* type is builtin (int, string, untyped, ...)*/
						annotateSyntaxElementAs(NimSyntaxHighlighter.BUILTIN_TYPE)
					else
						/* custom type name */
						annotateSyntaxElementAs(NimSyntaxHighlighter.TYPE)
				} else if (psi.text == "result")
					annotateSyntaxElementAs(NimSyntaxHighlighter.RESULT_PARAM)
			}
			is IdentPragmaPairImpl -> {
				/* param in function */
				if (psi.context is IdentifierDefsImpl)
					annotateSyntaxElementAs(NimSyntaxHighlighter.FUNCTION_PARAMETER)
			}
		}

		when (psi.node.elementType) {
			OPERATOR -> when (psi.context) {
				is ProcDefImpl ->
					annotateSyntaxElementAs(NimSyntaxHighlighter.PUBLIC_OPERATOR)
			}
		}
	}
}
