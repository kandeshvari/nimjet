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
import java.util.ArrayList


class NimBlock(node: ASTNode, wrap: Wrap?, alignment: Alignment?, sb: SpacingBuilder) : AbstractBlock(node, wrap, alignment) {
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
		// if we have `\n` before node - align child elements by this node
		return if (node.treePrev != null && node.treePrev.textContains('\n')) {
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

		if (node.getUserData(keyAlignment) != null) {
			println("Parent: %s; El type: %s; aligned: %s; text: %s\n".format(
				node.treeParent.elementType.toString(),
				node.elementType.toString(),
				(node.getUserData(keyAlignment) != null).toString(),
				node.text
			))
		}


		// do not set indent on top-level elements
		if (node.treeParent?.elementType.toString() == "FILE")
			return Indent.getNoneIndent()

//                if ((node.elementType == LINE_COMMENT || node.elementType == DOC_COMMENT))
//                        return null

		// case statement
		// set continuanion indent for `of` block
		if ((node.elementType == BLOCK)
			&& node.treeParent != null
			&& (node.treeParent.elementType == CASE_STMT || node.treeParent.elementType == CASE_EXPR)) {
//                        println("CASE BLOCK INDENT: %s; text: %s\n".format(
//                                node.elementType.toString(),
//                                node.text
//                        ))
			return Indent.getContinuationIndent(false)
		}

		// do not set extra indent for else in `case` stmt
		if ((node.elementType == T_ELSE)
			&& node.treeParent != null
			&& (node.treeParent.elementType == CASE_STMT || node.treeParent.elementType == CASE_EXPR)) {
//                        println("CASE ELSE INDENT: %s; text: %s\n".format(
//                                node.elementType.toString(),
//                                node.text
//                        ))
			return Indent.getNormalIndent()
		}

		/* indent continuation lists
		* test1: indent_continuation_lists.nim
		* */
		fun directChildOf(elementType: IElementType, parentIElementType: IElementType): Boolean =
			node.elementType == elementType && node.treeParent != null && node.treeParent.elementType == parentIElementType

		fun directChildOf(elementType: IElementType, parentIElementTypes: TokenSet): Boolean =
			node.elementType == elementType && node.treeParent != null && node.treeParent.elementType in parentIElementTypes

		fun upwardChildOf(elementType: IElementType, parentIElementType: IElementType): Boolean {
			if (node.elementType == elementType) {
				var p = node.treeParent
				while (p != null ) {
					if (p.elementType == parentIElementType) return true
					p = p.treeParent
				}
			}
			return false
		}

		fun upwardChildOf(elementType: IElementType, parentIElementTypes: TokenSet): Boolean {
			if (node.elementType == elementType) {
				var p = node.treeParent
				while (p != null ) {
					if (p.elementType in parentIElementTypes) return true
					p = p.treeParent
				}
			}
			return false
		}

		fun spaceIndent(elementType: IElementType? = null, relativeToDirectParent: Boolean = true): Indent {
			return if (elementType == null) {
				Indent.getSpaceIndent(node.treeParent.firstChildNode.psi.startOffsetInParent, relativeToDirectParent)
			} else {
				Indent.getSpaceIndent(node.treeParent.findChildByType(elementType)!!.psi.startOffsetInParent, relativeToDirectParent)
			}
		}

		val parents = TokenSet.create(IF_EXPR, TYPE_DESC, PREFIX_EXPR, EXPR_LIST, IDENTIFIER_DEFS, COMMAND_EXPR, ROUTINE_PARAM_LIST)
		if (upwardChildOf(node.elementType, parents))
			return spaceIndent()

		val parentsAligned = TokenSet.create(OBJECT_CTOR)
		if (upwardChildOf(node.elementType, parentsAligned))
			return spaceIndent(node.elementType)


//		if (upwardChildOf(node.elementType, TYPE_DESC)) {
//			println("FOUND CHILD OF TYPE_DESC: ${node.elementType} " +
//				"indent: ${node.treeParent.firstChildNode.psi.startOffsetInParent.toString()} " +
//				"text:[${node.text}]")
//			return spaceIndent()
//		}

//		if (directChildOf(IDENTIFIER_EXPR, EXPR_LIST))
//			return Indent.getSpaceIndent(node.treeParent.firstChildNode.psi.startOffsetInParent, true)
//
//		if (directChildOf(COMMAND_EXPR, EXPR_LIST))
//			return Indent.getSpaceIndent(node.treeParent.firstChildNode.psi.startOffsetInParent, true)

//
//		if (directChildOf(node.elementType, EXPR_LIST))
//			return Indent.getSpaceIndent(node.treeParent.firstChildNode.psi.startOffsetInParent, true)
//
//
//		if (directChildOf(node.elementType, IDENTIFIER_DEFS))
//			return Indent.getSpaceIndent(node.treeParent.firstChildNode.psi.startOffsetInParent, true)
//
//
//		if (node.elementType == CTOR_ARG || node.elementType == IDENTIFIER_DEFS) {
//			val param = node.treeParent?.findChildByType(node.elementType)
//			return Indent.getSpaceIndent(param?.psi?.startOffsetInParent!!, true)
//		}
		/* end indent continuation lists */

		// dont set indentation for specific elements (else, elif)
		if (node.elementType in AVOID_INDENT_TOKENS)
			return Indent.getNoneIndent()

		// general block indent
		if (node.treeParent != null && node.treePrev != null
			&& node.treeParent.elementType in BLOCK_START_TOKENS
			&& node.treePrev.textContains('\n')) {
			println("INDENT: %s; text: %s\n".format(
				node.elementType.toString(),
				node.text
			))
			return Indent.getNormalIndent()
		}

		// Don't indent others
		return Indent.getNoneIndent()
	}
}
