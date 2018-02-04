package org.nimjet.psi.ref

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import com.intellij.util.PlatformIcons
import generated.psi.Identifier

class IdentifierReference(element: Identifier) : PsiReferenceBase<Identifier>(element, TextRange.from(0, element.textLength)) {
	override fun getVariants(): Array<Any> {
		println("getVariants")
		// TODO: build completion list here
		return arrayOf(
			LookupElementBuilder.create("xxx1")
			.setIcon(PlatformIcons.VARIABLE_ICON)
			.setTypeText("Map<String,Object>"),
			LookupElementBuilder.create("xxx2")
			.setIcon(PlatformIcons.VARIABLE_ICON)
			.setTypeText("Root Object"),
			LookupElementBuilder.create("xxx3")
			.setIcon(PlatformIcons.VARIABLE_ICON)
			.setTypeText("<Current Object>"))
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
