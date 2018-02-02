package org.nimjet.psi.ref

import com.intellij.codeInsight.completion.InsertHandler
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import com.intellij.util.IncorrectOperationException
import generated.psi.Identifier
import java.util.stream.Collectors

class IdentifierReference(element: Identifier) : PsiReferenceBase<Identifier>(element, TextRange.from(0, element.getTextLength())) {

	override fun resolve(): PsiElement? {
		val resolver = symbolResolver
		val identifier = element
		val scope = ImportProcessor.buildImportScope(identifier)
		NimPsiTreeUtil.walkUp(resolver, identifier, {
			val index = RoutineIndex.INSTANCE
			val project = identifier.getProject()
			index.get(identifier.getText(), project, scope)
		}, identifier.getText(), scope)
		return resolver.getResolvedTarget()
	}

	override fun getVariants(): Array<Any> {
		val collector = SymbolCollector.withFilter({ el -> !(el is TypeDef || el is GenericParam) })
		NimPsiTreeUtil.walkUp(collector, element, {
			val index = RoutineIndex.INSTANCE
			val project = element.getProject()
			val scope = ImportProcessor.buildImportScope(element)
			index.getAllKeys(project).stream()
				.flatMap({ key -> index.get(key, project, scope).stream() })
				.collect(Collectors.toList<T>())
		})
		return collector.getLookupElements().stream().map({ le ->
			if (le.getPsiElement() is RoutineDef)
				return@collector.getLookupElements().stream().map(le as LookupElementBuilder).withInsertHandler(getRoutineInsertHandler(false))
			le
		}).toArray()
	}

	@Throws(IncorrectOperationException::class)
	override fun handleElementRename(newElementName: String): PsiElement {
		if (resolve() is ProcResultPsiElement)
			throw IncorrectOperationException("Cannot rename the implicit variable 'result'.")

		val newNode = ElementFactory.createIdentNode(element.getProject(), newElementName)
		val elemNode = element.getNode()
		elemNode.replaceChild(elemNode.findChildByType(ElementTypes.IDENT), newNode)
		return element
	}

	companion object {

		fun getRoutineInsertHandler(ignoreFirstArg: Boolean): InsertHandler<LookupElement> {
			return { context, item ->
				val editor = context.getEditor()
				val document = editor.getDocument()
				var offset = context.getTailOffset()
				document.insertString(offset, "()")
				val def = (item.getPsiElement() as RoutineDef)!!
				if (ignoreFirstArg)
					offset += if (def!!.getMaxParameterCount() === 1) 2 else 1
				else
					offset += if (def!!.hasParams()) 1 else 2
				editor.getCaretModel().moveToOffset(offset)
			}
		}
	}
}
