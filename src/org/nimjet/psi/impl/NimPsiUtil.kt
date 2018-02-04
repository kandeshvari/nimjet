package org.nimjet.psi.impl

import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import org.nimjet.NimParserDefinition.Companion.FILE
import org.nimjet.psi.ElementTypes
import org.nimjet.psi.ElementTypes.*
import org.nimjet.psi.NimElementFactory
import org.nimjet.psi.ref.IdentifierReference
import org.nimjet.psi.ref.RoutineReference
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.FileTypeIndex
import com.intellij.util.indexing.FileBasedIndex
import generated.psi.*
import org.nimjet.NimFile
import org.nimjet.NimFileType
import org.nimjet.psi.NimNamedElement
import java.util.*
import kotlin.collections.ArrayList
import kotlin.reflect.KClass


class NimWalkingVisitor<T>(val klass: Class<T>) : PsiRecursiveElementWalkingVisitor() {
	companion object {
		inline operator fun <reified T : Any> invoke() = NimWalkingVisitor(T::class.java)
//		private inline fun <reified T> isType(type: PsiElement): Boolean {
//
//			return type is T
//		}
	}

	val identifiers: MutableList<T> = ArrayList<T>()

	private fun isType(t: Any): Boolean {
		return klass.isAssignableFrom(t.javaClass)
	}

	override fun visitElement(element: PsiElement) {
		if (isType(element)) identifiers.add(element as T)
		super.visitElement(element)
	}
}


class NimPsiUtil {
	companion object {
//		fun walkUp(e: PsiElement, walk: (e: PsiElement) -> Boolean): Boolean {
//			var p = e.parent
//			while (p.node.elementType != FILE) {
//				if (walk(p)) return true
//				p = p.parent
//			}
//			return false
//		}
//
//		fun findRoutines(project: Project, name: String): List<Identifier> {
//			val result: MutableList<Identifier> = ArrayList<Identifier>()
//			val virtualFiles =
//				FileBasedIndex.getInstance().getContainingFiles<FileType, Void>(FileTypeIndex.NAME, NimFileType,
//					GlobalSearchScope.allScope(project))
//			for (virtualFile in virtualFiles) {
//				val nimFile = PsiManager.getInstance(project).findFile(virtualFile) as NimFile?
//				if (nimFile != null) {
//					val identifiers = PsiTreeUtil.getChildrenOfType(nimFile, Identifier::class.java)
//					println("findRoutines: identifiers: $identifiers")
//					if (identifiers != null) {
//						identifiers.filterTo(result) {
//							it.parent is RoutineIdentifier && name == it.node.text
//						}
//					}
//				}
//			}
//			println("findRoutines: result: $result")
//			return result
//		}
//
//		fun findRoutines(root: PsiFile, name: String): List<Identifier> {
//			val visitor = NimWalkingVisitor<Identifier>()
//			root.accept(visitor)
//			val result: MutableList<Identifier> = ArrayList<Identifier>()
//			visitor.identifiers.filterTo(result) { it.parent is RoutineIdentifier && name == it.node.text }
//			println("findRoutines: result: $result")
//			return result
//		}
//
//
//		fun getReference(element: Identifier): PsiReference {
//			println("getReference: [${element.text}] [${element.parent.node.elementType}]")
//
//			when (element.parent.node.elementType) {
//				ROUTINE_IDENTIFIER -> {
//					val name = element.name
////					println("Find routine declaration")
//					return RoutineReference(element)
//				}
//			}
//
//			val p = walkUp(element) {
////				println("walkUp: ${it.node?.elementType}")
//				if (it.node?.elementType == COMMAND_EXPR || it.node?.elementType == CALL_EXPR) {
//					return@walkUp true
//				}
//				false
//			}
//
//			if (p) {
////				println("Found call/command expression")
//				return RoutineReference(element)
//			}
//
//			//		return new PsiReferenceBase.createSelfReference(element, element);
//			return IdentifierReference(element)
//		}
//
//		fun setName(element: Identifier, newName: String): PsiElement {
//			println("SET NAME: $newName")
//			return element
//		}
//
////
////		fun getUpperScope(e: PsiElement): PsiElement {
////			var p = e.parent
////
////			l1@while (p.node.elementType != FILE) {
////				return when (p.node) {
////					is ProcDef -> p
////					is MethodDef -> p
////					is BlockStmt -> p
////					is IfStmt -> p
////					is ElseStmtBranch -> p
////					is CaseBranch -> p
////					else -> {
////						p = p.parent
////						continue@l1
////					}
////				}
////			}
////			return p
////		}
////
////		fun findInScope(e: PsiElement, klass: KClass<PsiElement>, acc: MutableList<PsiElement>): MutableList<PsiElement> {
////			val res: MutableList<PsiElement> = ArrayList(e.children.filterIsInstance(klass.java))
////			val scope = getUpperScope(e)
////			acc.toCollection(res)
////			if (scope is NimFile)
////				return res
////			return findInScope(scope, klass, res)
////		}
	}
}
