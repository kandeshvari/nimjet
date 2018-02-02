package org.nimjet.psi.ref

import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceRegistrar
import com.intellij.psi.PsiReference
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiLiteralExpression
import com.intellij.util.ProcessingContext
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceProvider
import com.intellij.patterns.PlatformPatterns


object NimReferenceContributor : PsiReferenceContributor() {
	override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
		registrar.registerReferenceProvider(PlatformPatterns.psiElement(PsiLiteralExpression::class.java),
			object : PsiReferenceProvider() {
				override fun getReferencesByElement(element: PsiElement,
				                                    context: ProcessingContext): Array<PsiReference> {
					println("NimReferenceContributor")
					val literalExpression = element as PsiLiteralExpression
					val value = if (literalExpression.value is String)
						literalExpression.value as String?
					else
						null
					return if (value != null && value.startsWith("nim" + ":")) {
						arrayOf<PsiReference>(NimReference(element, TextRange(8, value.length + 1)))
					} else PsiReference.EMPTY_ARRAY
				}
			})
	}}
