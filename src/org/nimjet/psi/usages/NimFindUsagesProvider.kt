package org.nimjet.psi.usages

import com.intellij.lang.cacheBuilder.DefaultWordsScanner
import com.intellij.lang.cacheBuilder.WordsScanner
import com.intellij.lang.findUsages.FindUsagesProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.tree.TokenSet
import org.nimjet.NimParserDefinition.Companion.COMMENTS
import org.nimjet.parser.NimLexerAdapter
import org.nimjet.psi.ElementTypes.*

class NimFindUsagesProvider : FindUsagesProvider {
	override fun getWordsScanner(): WordsScanner? {
		// TODO: use a custom words scanner to support Nim style identifier matching
		return DefaultWordsScanner(NimLexerAdapter(), TokenSet.create(IDENT), COMMENTS, LITERALS)
	}

	override fun canFindUsagesFor(el: PsiElement): Boolean {
		return el is PsiNameIdentifierOwner
	}

	override fun getHelpId(psiElement: PsiElement): String? {
		return null
	}

	override fun getType(psiElement: PsiElement): String {
		return "symbol"
	}

	override fun getDescriptiveName(psiElement: PsiElement): String {
		if (psiElement is PsiNamedElement) {
			val name = psiElement.name
			if (name != null)
				return name
		}
		return psiElement.text
	}

	override fun getNodeText(element: PsiElement, b: Boolean): String {
		return if (element is PsiNamedElement) element.name!! else element.text
	}
}
