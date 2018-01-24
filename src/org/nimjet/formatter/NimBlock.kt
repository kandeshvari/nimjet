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
import org.nimjet.NimParserDefinition.Companion.WHITE_SPACES
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
		return if ((node.treePrev != null && node.treePrev.textContains('\n')
				|| node.elementType in ALIGNED_EXPRESSIONS )) {
			Alignment.createAlignment()
		} else
			null
//                                if (FormatterUtil.isPrecededBy(node, BLOCK_STMT))
//		return Alignment.createAlignment()

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

		fun directChildOf(elementType: IElementType, parentIElementType: IElementType): Boolean =
			node.elementType == elementType && node.treeParent?.elementType == parentIElementType

		fun directChildOf(elementType: IElementType, parentIElementTypes: TokenSet): Boolean =
			node.elementType == elementType && node.treeParent?.elementType in parentIElementTypes

		fun upwardChildOf(elementType: IElementType, parentIElementType: IElementType): Boolean {
			if (node.elementType == elementType) {
				var p = node.treeParent
				while (p != null) {
					if (p.elementType == parentIElementType) return true
					p = p.treeParent
				}
			}
			return false
		}

		fun upwardChildOf(elementType: IElementType, parentIElementTypes: TokenSet): Boolean {
			if (node.elementType == elementType) {
				var p = node.treeParent
				while (p != null) {
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

		// check is newline exists up till parent
		fun hasUpwardNewLine(): Boolean {
			var p = node.treePrev
			while (p != null){
				if (p.elementType in WHITE_SPACES && p.textContains('\n')) return true
				p = p.treePrev
			}
			return false
		}

		// do not set indent on top-level elements
		if (node.treeParent?.elementType == FILE) {
			return Indent.getNoneIndent()
		}


		// indent comments within parent block
		if (node.elementType in COMMENTS) {
			return Indent.getNormalIndent()
		}

		/* indent continuation lists */
		val parentsAligned = TokenSet.create(OBJECT_CTOR)
		if (directChildOf(node.elementType, parentsAligned))
			return spaceIndent(node.elementType)

		if (directChildOf(STMT_LIST_EXPR, EXPR_LIST))
			return spaceIndent()

		// TODO: settings UI
		if (directChildOf(node.elementType, EXPR_COLON_EQ_EXPR_LIST))
			return spaceIndent()


		/* end indent continuation lists */


		/* mass indent */
		// don't indent special cases
		if (directChildOf(T_ELSE, IF_EXPR))
			return Indent.getNoneIndent()

		// don't indent `of`, `elif`, `else` branches
		if (node.elementType in AVOID_INDENT)
			return Indent.getNoneIndent()


		// indent true blocks (block, if, let, var, ...)
		if (node.elementType in BLOCK_START_TOKENS && node.treePrev != null && node.treePrev.textContains('\n')) {
			return Indent.getNormalIndent()
		}

		// indent child elements if need
		if (node.elementType in CHILD_TOKENS && node.treePrev != null && node.treePrev.textContains('\n')) {
			return Indent.getNormalIndent()
		}





		/* indent continuation lists
		* test1: formatter.nim
		* */

//
//
//		val parents = TokenSet.create(VAR_DEF, CONST_DEF, IF_EXPR, TYPE_DESC, PREFIX_EXPR, EXPR_LIST, IDENTIFIER_DEFS, COMMAND_EXPR, ROUTINE_PARAM_LIST)
////		if (directChildOf(node.elementType, parents) && hasUpwardNewLine())
////			return Indent.getNormalIndent()
//
//		if (upwardChildOf(node.elementType, parents))
//			return spaceIndent()
//
//
//
//		if (upwardChildOf(node.elementType, TYPE_DESC)) {
//			println("FOUND CHILD OF TYPE_DESC: ${node.elementType} " +
//				"indent: ${node.treeParent.firstChildNode.psi.startOffsetInParent.toString()} " +
//				"text:[${node.text}]")
//			return spaceIndent()
//		}
//
//		if (directChildOf(IDENTIFIER_EXPR, EXPR_LIST))
//			return Indent.getSpaceIndent(node.treeParent.firstChildNode.psi.startOffsetInParent, true)
//
//		if (directChildOf(STMT_LIST_EXPR, EXPR_LIST))
//			return Indent.getSpaceIndent(node.treeParent.firstChildNode.psi.startOffsetInParent, false)
//
//		if (directChildOf(COMMAND_EXPR, EXPR_LIST))
//			return Indent.getSpaceIndent(node.treeParent.firstChildNode.psi.startOffsetInParent, true)
//
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
//		/* end indent continuation lists */


		// Don't indent others
//		println("NONE: ${node.elementType} (${node.treeParent?.elementType})")
		return Indent.getNoneIndent()
	}
}
