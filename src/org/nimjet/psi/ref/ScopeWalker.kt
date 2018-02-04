package org.nimjet.psi.ref

import com.intellij.psi.PsiElement
import generated.psi.*
import org.nimjet.NimFile
import org.nimjet.NimParserDefinition
import org.nimjet.psi.NimNamedElement

class ScopeWalker<T: PsiElement>(val klass: Class<T>) {
		fun getUpperScope(e: PsiElement): PsiElement {
			var p = e.parent

			l1@while (p.node.elementType != NimParserDefinition.FILE) {
//				println("NODE: $p")

				return when (p) {
					is ProcDef -> p
					is MethodDef -> p
					is BlockStmt -> p
					is IfStmt -> p
					is ElseStmtBranch -> p
					is CaseBranch -> p
					else -> {
						p = p.parent
						continue@l1
					}
				}
			}
			return p
		}

		fun findInScope(e: PsiElement, name: String): T? {
//			println("SCOPE: $e")
			val res = ArrayList(e.children.filterIsInstance(klass).filter {(it as NimNamedElement).name == name})
//			println("FOUND: ${res}")
			if (res.size != 0)
				return res[0]
			if (e is NimFile) {
				return null
			}
			return findInScope(getUpperScope(e), name)
		}

		fun findInScope(e: PsiElement): ArrayList<T> {
//			println("SCOPE: $e")
			val res = ArrayList(e.children.filterIsInstance(klass))
//			println("FOUND: ${res}")
			if (e is NimFile) return res
			return findInScope(getUpperScope(e))
		}
}
