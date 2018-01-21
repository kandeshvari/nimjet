package org.nimjet.formatter

import com.intellij.formatting.*
import com.intellij.lang.ASTNode
import com.intellij.psi.formatter.common.AbstractBlock
import com.intellij.formatting.WrapType
import com.intellij.psi.TokenType
import org.nimjet.psi.ElementTypes.*
import com.intellij.openapi.util.Key
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
                if (node.getUserData(keyAlignment) != null) {
                        println("Parent: %s; El type: %s; aligned: %s; text: %s\n".format(
                                node.treeParent.elementType.toString(),
                                node.elementType.toString(),
                                (node.getUserData(keyAlignment) != null).toString(),
                                node.text
                        ))
                }


                // do not set indent on top-level elements
                if (node.treeParent.elementType.toString() == "FILE")
                        return Indent.getNoneIndent()

                // case statement
                // set continuanion indent for `of` block
                if ((node.elementType == BLOCK)
                        && node.treeParent != null
                        && (node.treeParent.elementType == CASE_STMT || node.treeParent.elementType == CASE_EXPR)) {
                        println("CASE BLOCK INDENT: %s; text: %s\n".format(
                                node.elementType.toString(),
                                node.text
                        ))
                        return Indent.getContinuationIndent(false)
                }

                if ((node.elementType == T_ELSE)
                        && node.treeParent != null
                        && (node.treeParent.elementType == CASE_STMT || node.treeParent.elementType == CASE_EXPR)) {
                        println("CASE ELSE INDENT: %s; text: %s\n".format(
                                node.elementType.toString(),
                                node.text
                        ))
                        return Indent.getNormalIndent()
                }

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
