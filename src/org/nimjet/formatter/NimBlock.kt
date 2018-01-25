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
		// if we have `\n` before node - align child elements of this node
		return if ((node.treePrev != null && node.treePrev.textContains('\n')
				|| node.elementType in ALIGNED_EXPRESSIONS )) {
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
				Indent.getSpaceIndent(parent.findChildByType(elementType)!!.psi.startOffsetInParent, relativeToDirectParent)
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
		if (directChildOf(currentElementType, DIRECT_PARENTS_ALIGNED))
//			return spaceIndent(currentElementType)
			return spaceIndent()

		/* upward children */
		// TODO: settings UI
		if (upwardChildOf(currentElementType, UPWARD_PARENTS_ALIGNED))
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
}
