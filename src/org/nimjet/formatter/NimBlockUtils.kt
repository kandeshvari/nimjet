package org.nimjet.formatter

import com.intellij.formatting.Alignment
import com.intellij.formatting.Indent
import com.intellij.lang.ASTNode
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.PsiTreeUtil
import org.nimjet.NimParserDefinition.Companion.FILE
import org.nimjet.psi.ElementTypes.*

object NimBlockUtils {
	/* set of elements of statement lists; need for correct indentation */
	val SAME_INDENT = TokenSet.create(TYPE_DEF, VAR_DEF, CONST_DEF, EXPR_STMT, OBJECT_FIELDS, VAR_SECT, CONST_SECT)
	val DEDENT = TokenSet.create(RETURN_STMT, IDENTIFIER_EXPR, YIELD_STMT)
	val BLOCK_START_KEYWORDS = TokenSet.create(T_ADDR, T_AND, T_AS, T_ASM, T_ATOMIC, T_BIND, T_BLOCK, T_CAST,
		T_CONCEPT, T_CONST, T_CONTINUE, T_CONVERTER, T_DEFER, T_DISCARD, T_DISTINCT, T_DIV, T_DO, T_ELIF, T_ELSE, T_END,
		T_ENUM, T_EXCEPT, T_EXPORT, T_FINALLY, T_FOR, T_FROM, T_FUNC, T_GENERIC, T_IF, T_IMPORT, T_IN, T_INCLUDE,
		T_INTERFACE, T_IS, T_ISNOT, T_ITERATOR, T_LET, T_MACRO, T_METHOD, T_MIXIN, T_MOD, T_NIL, T_NOT, T_NOTIN, T_OBJECT,
		T_OF, T_OR, T_OUT, T_PROC, T_PTR, T_RAISE, T_REF, T_SHL, T_SHR, T_STATIC, T_TEMPLATE, T_TRY, T_TUPLE,
		T_TYPE, T_USING, T_VAR, T_WHEN, T_WHILE, T_WITH, T_WITHOUT, T_XOR)


	/**
	 *  Align inner parent node only if whitespaces before contains new-line:
	 *
	 *  |# current node == `object` element
	 *  |type X = object  # <- `object` non-aligned; `x:...` aligned by `type`
	 *  |  x: int
	 *
	 *  |type X =
	 *  |  object  # <- `object` aligned; `x:...` aligned by `object`
	 *  |    x: int
	 */
	fun decideAlignment(node: ASTNode): Alignment? {
		// if we have `\n` before node - align child elements of this node
		return if ((node.treePrev != null && node.treePrev.textContains('\n')
				|| node.elementType in ALIGNED_EXPRESSIONS)) {
			Alignment.createAlignment()
		} else
			null
	}

	fun getIdx(parent: ASTNode, idx: Int): ASTNode? {
		var p = parent.firstChildNode
		//println("D: getIdx: parent: ${parent.elementType}")
		var count = 0
		while (p != null) {
			if (p.elementType != TokenType.WHITE_SPACE) {
				if (count == idx) {
					//println("> D: getIdx: idx ${p.elementType} $count")
					return p
				}
				//println("D: getIdx: idx ${p.elementType} $count")
				count++
			}
			p = p.treeNext
		}
		return null
	}

	private fun getLastChild(parent: ASTNode, notError: Boolean = true): ASTNode {
		//println("D: getLastChild: parent ${parent.elementType}")
		var p = parent.lastChildNode
		while (p != null) {
			//println("D: getLastChild: ${p.elementType}")
			if (p.lastChildNode == null) {
//				if (notError && (p.elementType == ERROR_ELEMENT || p.elementType == WHITE_SPACE)) {
				if (notError && (p.elementType == TokenType.WHITE_SPACE)) {
					//println("return [${p.treePrev?.elementType}]")
					return p.treePrev
				}
				//println("return this")
				return p
			}
			p = p.lastChildNode
		}
		//println("D: getLastChild: not found. Return self")
		return parent
	}

	private fun ancestorOf(ancestorIElementTypes: TokenSet, node: ASTNode?): Boolean {
		if (node != null) {
			var p = node.treeParent
			while (p != null) {
				if (p.elementType in ancestorIElementTypes) return true
				p = p.treeParent
			}
		}
		return false
	}

	private fun ancestorOf(ancestorIElementType: IElementType, node: ASTNode?): Boolean {
		if (node != null) {
			var p = node.treeParent
			while (p != null) {
				if (p.elementType == ancestorIElementType) return true
				p = p.treeParent
			}
		}
		return false
	}

	private fun getAncestorOf(ancestorIElementType: IElementType, node: ASTNode): ASTNode? {
		var p = node.treeParent
		while (p != null) {
			if (p.elementType == ancestorIElementType) return p
			p = p.treeParent
		}
		return null
	}

	private fun debugIdx(parent: ASTNode, idx: Int) {
		var p = parent.firstChildNode
		//println("D: parent: ${parent.elementType}")
		var count = 0
		while (p != null) {
			if (p.elementType != TokenType.WHITE_SPACE) {
				if (count == idx) //print("curr IDX: ")
				//println("D: idx ${p.elementType} $count")
					count++
			}
			p = p.treeNext
		}
	}

	private fun getNodeIndent(node: ASTNode): Int {
		val prev = node.treePrev
		if (prev != null && prev.elementType == TokenType.WHITE_SPACE) {
			val indentWhitespaces = prev.text.substringAfterLast('\n', "x")

			if (indentWhitespaces != "x") {
				//println("D: findIndentedAncestor: indented [${node.elementType}:${indentWhitespaces.length}]")
				return indentWhitespaces.length
			}
		}
		return -1
	}

	private fun findIndentedAncestor(node: ASTNode, level: Int = 1): Pair<ASTNode, Int> {
		var p = node.treeParent
		var prevSave = node
		var counter = level

		while (p != null) {
			val nodeIndent = getNodeIndent(p)
			//println("D: findIndentedAncestor: curr node [${p.elementType}:${nodeIndent}]")
			if (nodeIndent != -1) {
				//print("D: findIndentedAncestor: found indented ancestor [${p.elementType}:${nodeIndent}]: ")
				if (--counter == 0) {
					//println("RETURNED")

					return Pair(p, nodeIndent)
				}
				//println("SKIPPED due counter")
			}
			prevSave = p
			p = p.treeParent
		}
		//println("D: findIndentedAncestor: indented ancestor not found, return [${prevSave.elementType}]")
		return Pair(prevSave, 0)
	}

	private fun findIndentedNeighbourOrAncestor(node: ASTNode, level: Int = 1): Pair<ASTNode, Int> {
		val prev = findIndentedNeighbour(node, level)
		if (prev != null) return prev
		//println("D: findIndentedNeighbourOrAncestor: indented prev not found, try to find indented ancestor")
		return findIndentedAncestor(node, level)
	}

	private fun findIndentedNeighbour(node: ASTNode, level: Int = 1): Pair<ASTNode, Int>? {
		//println("D: findIndentedNeighbour: we are [${node.elementType}]")
		var counter = level
		var p = node.treePrev

		/* skip first upper neighbour if it ERROR_ELEMENT */
		if (node.treeParent?.elementType == TokenType.ERROR_ELEMENT) {
			//println("D: findIndentedNeighbour: we are ERROR_ELEMENT, skipping")
			p = node.treeParent?.treePrev
		}

		while (p != null) {
			//println("D: findIndentedNeighbour: [${p.elementType}]")
			val nodeIndent = getNodeIndent(p)
			if (nodeIndent != -1) {
				if (--counter == 0) {
					//println("> D: findIndentedNeighbour: [${p.elementType}:$nodeIndent]")
					return Pair(p, nodeIndent)
				}
				//println("SKIPPED due counter")
			}
			p = p.treePrev
		}
		//println("D: findIndentedNeighbour: indented prev not found: return null")
		return null
	}

	private fun isAncestor(ancestor: ASTNode, node: ASTNode, strict: Boolean = true): Boolean {
		return PsiTreeUtil.isAncestor(ancestor.treeParent?.psi, node.psi, strict)
	}

	private fun getDiffIndent(successorNode: ASTNode, wsNode: ASTNode, extraIndent: Int = 0, level: Int = 1): Indent {
		val indentedAncestor = findIndentedNeighbourOrAncestor(successorNode, level)
		val wsIndentedAncestor = findIndentedAncestor(wsNode)
		val indent = indentedAncestor.second - wsIndentedAncestor.second + extraIndent
		//println("D: getDiffIndent: A:${indentedAncestor.first.elementType}; WS:${wsIndentedAncestor.first.elementType}; indent=$indent")
		return Indent.getSpaceIndent(indent, false)
	}

	private fun getPrevNotWhitespace(node: ASTNode): ASTNode? {
		var p = node.treePrev
		while (p != null && p.elementType == TokenType.WHITE_SPACE) p = p.treePrev
		return p
	}

	fun calculateIndent(prevNode: ASTNode): Indent {
		val indentSize = NimBlock.settings.indentOptions?.INDENT_SIZE ?: 4

		/* WS Node (current whitespace node ) */
		val wsNode = prevNode.treeNext

		/* last child of previuos node: !(ERROR_ELEMENT || WHITE_SPACE) **/
		val prevLastChild = getLastChild(prevNode)

		//println("[${prevNode.elementType}] [${prevLastChild.elementType}]")

		/* if prevLastChild is Keyword it means block start */
		if (prevLastChild.elementType in BLOCK_START_KEYWORDS
			|| prevLastChild.elementType == OPERATOR
			|| prevLastChild.elementType == T_EQ
		) {
			//println("D: calculateIndent: found KEYWORD:${prevLastChild.elementType}")
			if (prevNode.elementType == TokenType.ERROR_ELEMENT) {
				val prevErrorNonWhitespaceLastChild = getPrevNotWhitespace(prevNode.treePrev)
					?: return getDiffIndent(prevLastChild, wsNode, indentSize)
				val prevErrorLastChild = getLastChild(prevErrorNonWhitespaceLastChild)

				return getDiffIndent(prevErrorLastChild, wsNode, indentSize)
			}
			return getDiffIndent(prevLastChild, wsNode, indentSize)
		}

		/* get indented ancestor for WS node */
		/* we have not previous indented node, try to find indented ancestor */
		/* first indented ancestor node **/
		val firstIndentedNode = findIndentedNeighbourOrAncestor(prevLastChild)
		val ancestorElementType = firstIndentedNode.first.elementType
		//println("ancestorElementType: $ancestorElementType")
		val extraIndent = when (ancestorElementType) {
			in DEDENT -> {
				val assingmentExprNode = getAncestorOf(ASSIGNMENT_EXPR, prevLastChild)
				if (assingmentExprNode != null) {
					val indentedAncestor = findIndentedAncestor(assingmentExprNode)
					var ancestorIndent = getNodeIndent(indentedAncestor.first)
//					val indent = wsIndentedAncestor.second - ancestorIndent
					if (ancestorIndent < 0) ancestorIndent = 0
					//println("D: calculateIndent: found DEDENT; WS:${wsIndentedAncestor.second}; ASSIGNMENT_EXPR:$ancestorIndent; reduce indent by $indent");
					return Indent.getSpaceIndent(ancestorIndent, false)
				} else {
					// TODO: make return dedent smarter
					-indentSize
				}
			}
			in SAME_INDENT -> {
				//println("D: calculateIndent: found SAME_INDENT; indent=0");
				0
			}
			FILE -> 0
			in SECTIONS -> {
				if (prevLastChild.elementType !in KEYWORDS) {
					//println("D: calculateIndent: found one line SECTION; don't increase indent");
					0
				} else {
					//println("D: calculateIndent: found SECTION block; increase indent by $indentSize");
					indentSize
				}
			}
			else -> {
				//println("D: calculateIndent: common element; increase indent by $indentSize");
				indentSize
			}
		}

		return getDiffIndent(prevLastChild, wsNode, extraIndent)
	}

}
