package org.nimjet.parser

import com.intellij.lang.PsiBuilder
import com.intellij.lang.parser.GeneratedParserUtilBase
import com.intellij.openapi.util.Key
import com.intellij.psi.TokenType
import com.intellij.util.containers.IntIntHashMap


object NimParserUtil2 : GeneratedParserUtilBase() {
	private class ParserState(var builder: PsiBuilder) {
		var currentIndent = 0
		var pragmaCount = 0
		var primaryMode = PrimaryMode.NORMAL
		var semiStmtListCount = 0

		init {
			println("XXX (ParserState) inited")
		}

		private val tokIndentCache = IntIntHashMap()


		fun getTokenIndent(): Int {
			val tokStart = builder.currentOffset
			println("XXX INDENT tokStart %d".format(tokStart))

			if (tokIndentCache.containsKey(tokStart)) {
				println("XXX INDENT cache HIT(tokStart) %d".format(tokIndentCache.get(tokStart)))
				return tokIndentCache.get(tokStart)
			}

			var indent = -1
			val ws = getPrecedingWhiteSpace()
			val nlPos = ws.lastIndexOf('\n')
			if (nlPos != -1)
				indent = ws.length - nlPos - 1
			tokIndentCache.put(tokStart, indent)
			println("XXX tokIndentCache size %d".format(tokIndentCache.size()))
			println(tokIndentCache.keys())
			println(">>> values")
			println(tokIndentCache.values)
			return indent
		}

		internal enum class PrimaryMode {
			NORMAL, TYPE_DEF, TYPE_DESC
		}

		private fun getPrecedingWhiteSpace(): String {
			var wsOffset = 0
			while (builder.rawLookup(wsOffset - 1) === TokenType.WHITE_SPACE) {
				println("XXX SPACES loop %d".format(wsOffset))
				--wsOffset
			}
			val wsStart = builder.rawTokenTypeStart(wsOffset)
				println("XXX SPACES token_start %d".format(wsStart))

			return builder.originalText.subSequence(wsStart, builder.currentOffset).toString()
		}

		private fun getFollowingWhiteSpace(): String {
			var nonWsOffset = 1
			while (builder.rawLookup(nonWsOffset) === TokenType.WHITE_SPACE)
				++nonWsOffset
			val nonWsStart = builder.rawTokenTypeStart(nonWsOffset)
			return builder.getOriginalText().subSequence(builder.rawTokenTypeStart(1), nonWsStart).toString()
		}


		private fun getSpacesLeft(): Int {
			return getPrecedingWhiteSpace().length
		}

		internal fun getSpacesRight(): Int {
			return getFollowingWhiteSpace().length
		}


	}

	private var state: ParserState? = null
	private var stateSet = false


	private val parserStateKey = Key<ParserState>("parser-state")



	@JvmStatic
	fun indented(b: PsiBuilder, level: Int, parser: GeneratedParserUtilBase.Parser): Boolean {
		initParserState(b)
		println("XXX (indented) builder %s".format(b.toString()))
		println("XXX (indented) level %d".format(level))
		val tokIndent = state!!.getTokenIndent()
		println("XXX (indented): indentation/current: %d/%d".format(tokIndent, state!!.currentIndent))
		if (tokIndent > state!!.currentIndent) {
			val prevIndent = state!!.currentIndent
			val prevState = state

			state!!.currentIndent = tokIndent
			println("XXX (indented) ENTER PARSER %d".format(state!!.currentIndent))

			state = ParserState(b)
			val result = parser.parse(b, level + 1)
			state = prevState
//			println("XXX (indented) parser %s".format())
			state!!.currentIndent = prevIndent
			println("XXX (indented) LEAVE PARSER %d".format(state!!.currentIndent))
			println("XXX (indented) result %b".format(result))
			return result
		}
		return false
	}

	private fun initParserState(b: PsiBuilder) {
		println("XXX PARSER STATE INITIALIZED")
		if (state == null) state = ParserState(b)
	}


	@JvmStatic
	fun indNone(b: PsiBuilder, level: Int): Boolean {
		initParserState(b)
//		println("XXX indNone level %d".format(level))
//		println("XXX (indNone)(-1 true): indentation/current: %d/%d".format(state!!.getTokenIndent(b), state!!.currentIndent))
		val result = state!!.getTokenIndent() == -1
//		println("XXX (indNone) level %b".format(result))
		return result
	}

	@JvmStatic
	fun indEq(b: PsiBuilder, level: Int): Boolean {
		initParserState(b)
//		println("XXX indEq level %d".format(level))
//		println("XXX (indEq)(eq true): indentation/current: %d/%d".format(state!!.getTokenIndent(b), state!!.currentIndent))
		return state!!.getTokenIndent() == state!!.currentIndent
	}

	@JvmStatic
	fun indLt(builder: PsiBuilder, level: Int): Boolean {
		val tokenIndent = state!!.getTokenIndent()
		return tokenIndent >= 0 && tokenIndent < state!!.currentIndent
	}

	@JvmStatic
	fun indGt(builder: PsiBuilder, level: Int): Boolean {
		return state!!.getTokenIndent() > state!!.currentIndent
	}

	@JvmStatic
	fun indOpt(builder: PsiBuilder, level: Int): Boolean {
		val tokInd = state!!.getTokenIndent()
		return tokInd == -1 || tokInd > state!!.currentIndent
	}

	@JvmStatic
	fun enterPragma(builder: PsiBuilder, level: Int, parser: GeneratedParserUtilBase.Parser): Boolean {
		state!!.pragmaCount++
		val result = parser.parse(builder, level + 1)
		state!!.pragmaCount--
		return result
	}

	@JvmStatic
	fun inPragma(builder: PsiBuilder, level: Int): Boolean {
		return state!!.pragmaCount > 0
	}

	@JvmStatic
	fun typeDefMode(builder: PsiBuilder, level: Int, parser: GeneratedParserUtilBase.Parser): Boolean {
		val prevMode = state!!.primaryMode
		state!!.primaryMode = ParserState.PrimaryMode.TYPE_DEF
		val result = parser.parse(builder, level + 1)
		state!!.primaryMode = prevMode
		return result
	}

	@JvmStatic
	fun typeDescMode(builder: PsiBuilder, level: Int, parser: GeneratedParserUtilBase.Parser): Boolean {
		val prevMode = state!!.primaryMode
		state!!.primaryMode = ParserState.PrimaryMode.TYPE_DESC
		val result = parser.parse(builder, level + 1)
		state!!.primaryMode = prevMode
		return result
	}

	@JvmStatic
	fun inNormalMode(builder: PsiBuilder, level: Int): Boolean {
		return state!!.primaryMode == ParserState.PrimaryMode.NORMAL
	}

	@JvmStatic
	fun inTypeDefMode(builder: PsiBuilder, level: Int): Boolean {
		return state!!.primaryMode == ParserState.PrimaryMode.TYPE_DEF
	}

//	@JvmStatic
//	fun extendCommand(builder: PsiBuilder, level: Int, children: GeneratedParserUtilBase.Parser): Boolean {
//		val leftNode = builder.latestDoneMarker
//		if (leftNode!!.tokenType !== COMMAND_EXPR)
//			return false
//		val left = leftNode as PsiBuilder.Marker?
//		val newMarker = left!!.precede()
//		val result = children.parse(builder, level + 1)
//		if (result) {
//			left.drop()
//			newMarker.done(COMMAND_EXPR)
//		} else
//			newMarker.drop()
//
//		return result
//	}

//	@JvmStatic
//	fun parseSimpleExpr(builder: PsiBuilder, level: Int, primary: GeneratedParserUtilBase.Parser, limit: Int): Boolean {
//		val marker = builder.mark()
//		if (!primary.parse(builder, level + 1)) {
//			marker.drop()
//			return false
//		}
//		return parseOperators(builder, level + 1, primary, marker, limit)
//	}

//	@JvmStatic
//	private fun parseOperators(builder: PsiBuilder, level: Int, primary: GeneratedParserUtilBase.Parser, headMarker: PsiBuilder.Marker, limit: Int): Boolean {
//		var headMarker = headMarker
//		var opPrec = getPrecedence(builder.tokenType, builder.tokenText)
//
//		val mode = state.primaryMode
//		state.primaryMode = if (mode == ParserState.PrimaryMode.TYPE_DEF) ParserState.PrimaryMode.TYPE_DESC else mode
//		try {
//			while (opPrec >= limit && state.getTokenIndent(builder) < 0 && !isUnary(builder)) {
//				val leftAssoc = if (isRightAssociative(builder.tokenType, builder.tokenText)) 0 else 1
//				builder.advanceLexer()
//				var r = indOpt(builder, limit + 1)
//				if (r) r = parseSimpleExpr(builder, level + 1, primary, opPrec + leftAssoc)
//				if (r) {
//					headMarker.done(INFIX_EXPR)
//					headMarker = headMarker.precede()
//				} else {
//					headMarker.drop()
//					return false
//				}
//				opPrec = getPrecedence(builder.tokenType, builder.tokenText)
//			}
//			headMarker.drop()
//			return true
//		} finally {
//			state.primaryMode = mode
//		}
//	}

//	@JvmStatic
//	private fun isRightAssociative(tokType: IElementType?, tokText: String?): Boolean {
//		return tokType === OPERATOR && tokText!!.startsWith("^")
//	}
//
//	@JvmStatic
//	private fun isUnary(b: PsiBuilder): Boolean {
//		if (b.tokenType === OPERATOR || b.tokenType === T_DOTDOT) {
//			if (getSpacesLeft(b) > 0 && getSpacesRight(b) == 0)
//				return true
//		}
//		return false
//	}

//	@JvmStatic
//	private fun getPrecedence(tokType: IElementType?, tokText: String?): Int {
//		if (tokType === OPERATOR) {
//			if (tokText!!.endsWith("->") || tokText.endsWith("~>") || tokText.endsWith("=>"))
//				return 1
//
//			val relevantChar = tokText[0]
//			val assignment = tokText.endsWith("=")
//
//			when (relevantChar) {
//				'$', '^' -> return if (assignment) 1 else 10
//				'*', '%', '/', '\\' -> return if (assignment) 1 else 9
//				'~' -> return 8
//				'+', '-', '|' -> return if (assignment) 1 else 8
//				'&' -> return if (assignment) 1 else 7
//				'=', '<', '>', '!' -> return 5
//				'.' -> return if (assignment) 1 else 6
//				'?' -> return 2
//				else -> return if (assignment) 1 else 2
//			}
//		} else if (tokType === T_DIV || tokType === T_MOD || tokType === T_SHL || tokType === T_SHR)
//			return 9
//		else if (tokType === T_IN || tokType === T_NOTIN || tokType === T_IS || tokType === T_ISNOT || tokType === T_NOT || tokType === T_OF || tokType === T_AS)
//			return 5
//		else if (tokType === T_DOTDOT)
//			return 6
//		else if (tokType === T_AND)
//			return 4
//		else if (tokType === T_OR || tokType === T_XOR || tokType === T_PTR || tokType === T_REF)
//			return 3
//
//		return -10
//	}
//
//	@JvmStatic
//	fun arrow(builder: PsiBuilder, level: Int): Boolean {
//		if (builder.tokenType === OPERATOR && "->" == builder.tokenText) {
//			builder.advanceLexer()
//			return true
//		}
//		return false
//	}

	@JvmStatic
	fun enterSemiStmtList(builder: PsiBuilder, level: Int, parser: GeneratedParserUtilBase.Parser): Boolean {
		state!!.semiStmtListCount++
		val result = parser.parse(builder, level)
		state!!.semiStmtListCount--
		return result
	}

	@JvmStatic
	fun inSemiStmtList(builder: PsiBuilder, level: Int): Boolean {
		return state!!.semiStmtListCount > 0
	}

}
