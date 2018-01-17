package org.nimjet

import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.FlexAdapter
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet
import org.nimjet.parser.NimLexer
import org.nimjet.parser.NimParser
import org.nimjet.psi.ElementTypes

class NimParserDefinition : ParserDefinition {
        companion object {
                @JvmStatic
                val WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE)
                @JvmStatic
                val COMMENTS = TokenSet.create(ElementTypes.DOC_BLOCK_COMMENT, ElementTypes.BLOCK_COMMENT, ElementTypes.DOC_COMMENT, ElementTypes.LINE_COMMENT)
                @JvmStatic
                val FILE = IFileElementType(NimLanguage)
        }

        override fun createParser(p0: Project?): PsiParser = NimParser()

        override fun createFile(p0: FileViewProvider): PsiFile = NimFile(p0)

        override fun spaceExistanceTypeBetweenTokens(p0: ASTNode?, p1: ASTNode?): ParserDefinition.SpaceRequirements {
                return ParserDefinition.SpaceRequirements.MAY
        }

        override fun getStringLiteralElements(): TokenSet = TokenSet.EMPTY

        override fun getWhitespaceTokens(): TokenSet = WHITE_SPACES


        override fun getFileNodeType(): IFileElementType = FILE

        override fun createLexer(p0: Project?): Lexer = FlexAdapter(NimLexer())

        override fun createElement(p0: ASTNode?): PsiElement {
//                return ElementFactory.createElement(p0);
                return ElementTypes.Factory.createElement(p0);
        }

        override fun getCommentTokens(): TokenSet = COMMENTS
}
