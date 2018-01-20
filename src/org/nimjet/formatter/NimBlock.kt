package org.nimjet.formatter

import com.intellij.formatting.*
import com.intellij.lang.ASTNode
import com.intellij.psi.formatter.common.AbstractBlock
import com.intellij.formatting.WrapType
import com.intellij.psi.TokenType
import org.nimjet.psi.ElementTypes.*
import java.util.ArrayList


class NimBlock(node: ASTNode, wrap: Wrap?, alignment: Alignment?, sb: SpacingBuilder) : AbstractBlock(node, wrap, alignment) {

        private var spacingBuilder: SpacingBuilder = sb

        override fun isLeaf(): Boolean = myNode.firstChildNode == null;

        override fun getSpacing(p0: Block?, p1: Block): Spacing? = spacingBuilder.getSpacing(this, p0, p1);

        override fun buildChildren(): MutableList<Block> {
                val blocks = ArrayList<Block>()
                var child: ASTNode? = myNode.firstChildNode
                while (child != null) {
                        if (child.elementType !== TokenType.WHITE_SPACE) {
                                val block = NimBlock(child, Wrap.createWrap(WrapType.NONE, false), Alignment.createAlignment(),
                                        spacingBuilder)
                                blocks.add(block)
                        }
                        child = child.treeNext
                }
                return blocks
        }

        override fun getIndent(): Indent? {
//                println("Parent: %s; El type: %s; text: %s\n".format(
//                        node.treeParent.elementType.toString(),
//                        node.elementType.toString(),
//                        node.text
//                ))
                if (node.elementType in BLOCK_DEFS)
                        return Indent.getNormalIndent()
                return Indent.getNoneIndent()
        }
}
