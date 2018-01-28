package org.nimjet.formatter

import com.intellij.formatting.*
import com.intellij.lang.ASTNode
import com.intellij.psi.formatter.common.AbstractBlock
import com.intellij.formatting.WrapType
import com.intellij.psi.TokenType
import org.nimjet.psi.ElementTypes.*
import com.intellij.openapi.util.Key
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import org.nimjet.NimParserDefinition.Companion.COMMENTS
import org.nimjet.NimParserDefinition.Companion.FILE
import java.util.ArrayList
import com.intellij.psi.TokenType.ERROR_ELEMENT
import com.intellij.psi.TokenType.WHITE_SPACE
import com.intellij.openapi.diagnostic.Logger
import com.intellij.psi.util.PsiTreeUtil


class NimBlock(node: ASTNode, wrap: Wrap?, alignment: Alignment?, sb: SpacingBuilder) : AbstractBlock(node, wrap, alignment) {
	val LOG = Logger.getInstance("BLOCK")

	companion object {
		private val keyAlignment = Key<Alignment>("alignment")
	}

	private var spacingBuilder: SpacingBuilder = sb

	override fun isLeaf(): Boolean = myNode.firstChildNode == null;

	override fun getSpacing(p0: Block?, p1: Block): Spacing? = spacingBuilder.getSpacing(this, p0, p1);

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
	private fun decideAlignment(node: ASTNode): Alignment? {
		// if we have `\n` before node - align child elements of this node
		return if ((node.treePrev != null && node.treePrev.textContains('\n')
				|| node.elementType in ALIGNED_EXPRESSIONS)) {
			Alignment.createAlignment()
		} else
			null
	}

	override fun buildChildren(): MutableList<Block> {
		val blocks = ArrayList<Block>()
		var child: ASTNode? = myNode.firstChildNode
		while (child != null) {
			if (child.elementType !== TokenType.WHITE_SPACE) {
				val alignment = decideAlignment(child)
				val block = NimBlock(child, Wrap.createWrap(WrapType.NONE, false), alignment,
					spacingBuilder)
				// save alignment data for calculation indents
				child.putUserData(keyAlignment, alignment)
				blocks.add(block)
			}
			child = child.treeNext
		}
		return blocks
	}

	override fun getIndent(): Indent? {

		val currentElementType = node.elementType
		val parent = node.treeParent
		/**
		 * need to implement
		 * - https://nim-lang.org/docs/tut2.html#macros-statement-macros after annotator implementation
		 * -
		case ty.kind
		of tyPtr, tyPointer, tyChar, tyBool, tyEnum, tyCString,
		tyCString, tyInt..tyUInt64, tyRange, tyVar, tyLent:
		linefmt(p, cpsStmts, "$1 = $2;$n", rdLoc(dest), rdLoc(src))
		else: internalError("genAssignment: " & $ty.kind)

		 *
		 *
		 * */

		fun directChildOf(elementType: IElementType, parentIElementType: IElementType): Boolean =
			currentElementType == elementType && parent?.elementType == parentIElementType

		fun directChildOf(elementType: IElementType, parentIElementTypes: TokenSet): Boolean =
			currentElementType == elementType && parent?.elementType in parentIElementTypes

		fun upwardChildOf(elementType: IElementType, parentIElementType: IElementType): Boolean {
			if (currentElementType == elementType) {
				var p = parent
				while (p != null) {
					if (p.elementType == parentIElementType) return true
					p = p.treeParent
				}
			}
			return false
		}

		fun upwardChildOf(elementType: IElementType, parentIElementTypes: TokenSet): Boolean {
			if (currentElementType == elementType) {
				var p = parent
				while (p != null) {
					if (p.elementType in parentIElementTypes) return true
					p = p.treeParent
				}
			}
			return false
		}

		fun spaceIndent(elementType: IElementType? = null, relativeToDirectParent: Boolean = true): Indent {
			return if (elementType == null) {
				Indent.getSpaceIndent(parent.firstChildNode.psi.startOffsetInParent, relativeToDirectParent)
			} else {
				val firstChildByType = parent.findChildByType(elementType)
				if (firstChildByType != null) {
					Indent.getSpaceIndent(firstChildByType.psi.startOffsetInParent, relativeToDirectParent)
				} else {
					Indent.getSpaceIndent(parent.firstChildNode.psi.startOffsetInParent, relativeToDirectParent)
				}
			}
		}

		// do not set indent on top-level elements
		if (parent?.elementType == FILE) {
			return Indent.getNoneIndent()
		}


		// indent comments within parent block
		if (currentElementType in COMMENTS) {
			return Indent.getNormalIndent()
		}

		/* indent continuation lists */

		/* direct childen */
		// TODO: settings UI
		if (directChildOf(currentElementType, ARGS_ALIGNED_BY_FIND))
		// find first element of currentElementType in parent(ARGS_ALIGNED_BY_FIND) and indent by it
			return spaceIndent(currentElementType)

		// TODO: settings UI
		if (directChildOf(currentElementType, ARGS_ALIGNED_BY_ANY_FIRST))
		// just indent by first parent(ARGS_ALIGNED_BY_ANY_FIRST) child
			return spaceIndent()

		/* upward children */
		// TODO: settings UI
		if (upwardChildOf(currentElementType, UPWARD_PARENTS_ALIGNED))
		// do indent by first parent child if we are sub-child of UPWARD_PARENTS_ALIGNED... uhh, weird :(
			return spaceIndent()

		/* end indent continuation lists */


		/* common indent */

		// don't indent `of`, `elif`, `else`, `do` branches
		if (currentElementType in AVOID_INDENT)
			return Indent.getNoneIndent()


		val prev = node.treePrev

		// indent true blocks (block, if, let, var, ...)
		if (currentElementType in BLOCK_START_TOKENS && prev != null && prev.textContains('\n')) {
			return Indent.getNormalIndent()
		}

		// indent child elements if need
		if (currentElementType in CHILD_TOKENS && prev != null && prev.textContains('\n')) {
			return Indent.getNormalIndent()
		}

		// Don't indent others
		return Indent.getNoneIndent()
	}

	private fun getIdx(parent: ASTNode, idx: Int): ASTNode? {
		var p = parent.firstChildNode
		//println("D: getIdx: parent: ${parent.elementType}")
		var count = 0
		while (p != null) {
			if (p.elementType != WHITE_SPACE) {
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
				if (notError && (p.elementType == WHITE_SPACE)) {
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

	private fun debugIdx(parent: ASTNode, idx: Int) {
		var p = parent.firstChildNode
		//println("D: parent: ${parent.elementType}")
		var count = 0
		while (p != null) {
			if (p.elementType != WHITE_SPACE) {
				if (count == idx) //print("curr IDX: ")
				//println("D: idx ${p.elementType} $count")
				count++
			}
			p = p.treeNext
		}
	}

	private fun getNodeIndent(node: ASTNode): Int {
		val prev = node.treePrev
		if (prev != null && prev.elementType == WHITE_SPACE) {
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
		if (node.treeParent?.elementType == ERROR_ELEMENT) {
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
		while (p != null && p.elementType == WHITE_SPACE) p = p.treePrev
		return p
	}

	private fun calculateIndent(prevNode: ASTNode): Indent {
		// TODO: get INDENT_SIZE from settings
		val indentSize = 4

		/* WS Node (current whitespace node ) */
		val wsNode = prevNode.treeNext

		/* last child of previuos node: !(ERROR_ELEMENT || WHITE_SPACE) **/
		val prevLastChild = getLastChild(prevNode)

		/* set of elements of statement lists; need for correct indentation */
		val SAME_INDENT = TokenSet.create(TYPE_DEF, VAR_DEF, CONST_DEF, EXPR_STMT, OBJECT_FIELDS, VAR_SECT, CONST_SECT)
		val DEDENT = TokenSet.create(RETURN_STMT, IDENTIFIER_EXPR, YIELD_STMT)
		val BLOCK_START_KEYWORDS = TokenSet.create(T_ADDR, T_AND, T_AS, T_ASM, T_ATOMIC, T_BIND, T_BLOCK, T_CAST,
			T_CONCEPT, T_CONST, T_CONTINUE, T_CONVERTER, T_DEFER, T_DISCARD, T_DISTINCT, T_DIV, T_DO, T_ELIF, T_ELSE, T_END,
			T_ENUM, T_EXCEPT, T_EXPORT, T_FINALLY, T_FOR, T_FROM, T_FUNC, T_GENERIC, T_IF, T_IMPORT, T_IN, T_INCLUDE,
			T_INTERFACE, T_IS, T_ISNOT, T_ITERATOR, T_LET, T_MACRO, T_METHOD, T_MIXIN, T_MOD, T_NIL, T_NOT, T_NOTIN, T_OBJECT,
			T_OF, T_OR, T_OUT, T_PROC, T_PTR, T_RAISE, T_REF, T_SHL, T_SHR, T_STATIC, T_TEMPLATE, T_TRY, T_TUPLE,
			T_TYPE, T_USING, T_VAR, T_WHEN, T_WHILE, T_WITH, T_WITHOUT, T_XOR)


		//println("[${prevNode.elementType}]")


		/* if prevLastChild is Keyword it means block start */
		if (prevLastChild.elementType in BLOCK_START_KEYWORDS
			|| prevLastChild.elementType == OPERATOR
			|| prevLastChild.elementType == T_EQ
		) {
			//println("D: calculateIndent: found KEYWORD:${prevLastChild.elementType}")
			if (prevNode.elementType == ERROR_ELEMENT) {
				val prevErrorNonWhitespaceLastChild = getPrevNotWhitespace(prevNode.treePrev)
					?: return getDiffIndent(prevLastChild, wsNode, indentSize)
				val prevErrorLastChild = getLastChild(prevErrorNonWhitespaceLastChild)

				return getDiffIndent(prevErrorLastChild, wsNode, indentSize)
			}
			return getDiffIndent(prevLastChild, wsNode, indentSize)
		}

		/* get indented ancestor for WS node */
		val wsIndentedAncestor = findIndentedAncestor(prevNode.treeNext /* WS Node */)
		/* we have not previous indented node, try to find indented ancestor */
		/* first indented ancestor node **/
		val firstIndentedNode = findIndentedNeighbourOrAncestor(prevLastChild)
		val ancestorElementType = firstIndentedNode.first.elementType
		val extraIndent = when (ancestorElementType) {
			in DEDENT -> {
				val assingmentExprNode = getAncestorOf(ASSIGNMENT_EXPR, prevLastChild)
				if (assingmentExprNode != null) {
					val indentedAncestor = findIndentedAncestor(assingmentExprNode)
					var ancestorIndent = getNodeIndent(indentedAncestor.first)
					val indent = wsIndentedAncestor.second - ancestorIndent
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


	override fun getChildAttributes(newChildIndex: Int): ChildAttributes {
		val start = System.currentTimeMillis()

		//println("ENTER: parent: ${node.elementType}; idx=$newChildIndex")


		val prev = getIdx(node, newChildIndex - 1) ?: return ChildAttributes(Indent.getNoneIndent(), null)
		//println("WS PARENT [${node.elementType}]")
		val calculatedIndent = calculateIndent(prev)
		val diff = System.currentTimeMillis() - start
		println("getChildAttributes time: $diff ms")
		return ChildAttributes(calculatedIndent, null)
	}

	override fun isIncomplete(): Boolean {
		return false
	}
}

