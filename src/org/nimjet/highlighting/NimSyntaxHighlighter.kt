package org.nimjet.highlighting

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors as Default
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType

import org.nimjet.parser.NimLexerAdapter
import org.nimjet.psi.ElementTypes

class NimSyntaxHighlighter : SyntaxHighlighterBase() {
	companion object {
		val LINE_COMMENT = TextAttributesKey.createTextAttributesKey("NIM_LINE_COMMENT", Default.LINE_COMMENT)
		val BLOCK_COMMENT = TextAttributesKey.createTextAttributesKey("NIM_BLOCK_COMMENT", Default.BLOCK_COMMENT)
		val KEYWORD = TextAttributesKey.createTextAttributesKey("NIM_KEYWORD", Default.KEYWORD)
		val STRING = TextAttributesKey.createTextAttributesKey("NIM_STRING", Default.STRING)
		val NUMBER = TextAttributesKey.createTextAttributesKey("NIM_NUMBER", Default.NUMBER)
		val OPERATOR = TextAttributesKey.createTextAttributesKey("NIM_OPERATOR", Default.OPERATION_SIGN)
		val DOT = TextAttributesKey.createTextAttributesKey("NIM_DOT", Default.DOT)
		val COMMA = TextAttributesKey.createTextAttributesKey("NIM_COMMA", Default.COMMA)
		val PRAGMA = TextAttributesKey.createTextAttributesKey("NIM_PRAGMA", Default.PREDEFINED_SYMBOL)
		val BRACKETS = TextAttributesKey.createTextAttributesKey("NIM_BRACKET", Default.BRACKETS)
		val BRACES = TextAttributesKey.createTextAttributesKey("NIM_BRACES", Default.BRACES)
		val PARENTHESES = TextAttributesKey.createTextAttributesKey("NIM_PARENTHESES", Default.PARENTHESES)
		val DOC_COMMENTS = TextAttributesKey.createTextAttributesKey("NIM_DOC_COMMENT", Default.DOC_COMMENT)
		val FUNCTION_DECLARATION = TextAttributesKey.createTextAttributesKey("NIM_FUNCTION_DECLARATION", Default.FUNCTION_DECLARATION)
		val FUNCTION_CALL = TextAttributesKey.createTextAttributesKey("NIM_FUNCTION_CALL", Default.FUNCTION_CALL)
		val IDENTIFIER = TextAttributesKey.createTextAttributesKey("NIM_IDENTIFIER", Default.IDENTIFIER)

	}

	override fun getHighlightingLexer(): Lexer =
		NimLexerAdapter()

	override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey?> =
		pack(tokenMap(tokenType))

	private fun tokenMap(tokenType: IElementType): TextAttributesKey? = when (tokenType) {
		ElementTypes.T_COMMA -> COMMA
		ElementTypes.T_DOT -> DOT

		ElementTypes.IDENTIFIER -> IDENTIFIER
		ElementTypes.LINE_COMMENT -> LINE_COMMENT
		ElementTypes.BLOCK_COMMENT -> BLOCK_COMMENT

		in ElementTypes.DOC_COMMENTS -> DOC_COMMENTS
		in ElementTypes.STRINGS -> STRING
		in ElementTypes.KEYWORDS -> KEYWORD
		in ElementTypes.NUMBER_LITERALS -> NUMBER
		in ElementTypes.OPERATORS -> OPERATOR
		in ElementTypes.BRACKETS -> BRACKETS
		in ElementTypes.BRACES -> BRACES
		in ElementTypes.PARENTHESES -> PARENTHESES
		in ElementTypes.PROCS_DEF -> FUNCTION_DECLARATION
		in ElementTypes.PROCS_EXPR -> FUNCTION_CALL
		in ElementTypes.PRAGMAS -> PRAGMA

		else -> null
	}

}
