package org.nimjet.psi.ref

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import com.intellij.util.PlatformIcons
import generated.psi.Identifier
import generated.psi.ProcDef

class IdentifierReference(element: Identifier) : PsiReferenceBase<Identifier>(element, TextRange.from(0, element.textLength)) {
	override fun getVariants(): Array<Any> {
		println("getVariants")

		val routines =
			ScopeWalker<ProcDef>(ProcDef::class.java).findInScope(element.parent)

		return routines.map {
			val name = it.name ?: "null"
			LookupElementBuilder.create(name)
				.withIcon(PlatformIcons.FUNCTION_ICON)
				.withTypeText("Proc")
		}.toTypedArray()

//		val variables =
//			ScopeWalker<VarDef>(VarDef::class.java).findInScope(element.parent)
//
//		return variables.map {
//			val name = it.name ?: "null"
//			LookupElementBuilder.create(name)
//				.withIcon(PlatformIcons.FUNCTION_ICON)
//				.withTypeText("Proc")
//		}.toTypedArray()
	}

	override fun resolve(): PsiElement? {
//		println("resolve")
		return element
	}

//	@Throws(IncorrectOperationException::class)
//	override fun handleElementRename(newElementName: String): PsiElement {
//		println("handleElementRename")
//		return element
//	}

	companion object {

//		fun getRoutineInsertHandler(ignoreFirstArg: Boolean): InsertHandler<LookupElement> {
//			return { context, item ->
//				val editor = context.getEditor()
//				val document = editor.getDocument()
//				var offset = context.getTailOffset()
//				document.insertString(offset, "()")
//				val def = (item.getPsiElement() as RoutineDef)!!
//				if (ignoreFirstArg)
//					offset += if (def!!.getMaxParameterCount() === 1) 2 else 1
//				else
//					offset += if (def!!.hasParams()) 1 else 2
//				editor.getCaretModel().moveToOffset(offset)
//			}
//		}
	}
}
