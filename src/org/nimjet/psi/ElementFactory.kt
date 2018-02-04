package org.nimjet.psi

import com.intellij.lang.ASTNode
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.IncorrectOperationException
import generated.psi.ExprStmt
import generated.psi.Expression
import generated.psi.IdentifierExpr
import generated.psi.impl.*
import org.nimjet.NimFile
import org.nimjet.NimLanguage
import org.nimjet.psi.ElementTypes.*

object ElementFactory {
	fun createElement(node: ASTNode): PsiElement {
		val type = node.elementType
		when {
			type === ASM_STMT -> return AsmStmtImpl(node)
			type === ASSIGNMENT_EXPR -> return AssignmentExprImpl(node)
			type === BIND_STMT -> return BindStmtImpl(node)
			type === BLOCK_STMT -> return BlockStmtImpl(node)
			type === BRACKET_CTOR -> return BracketCtorImpl(node)
			type === BRACKET_EXPR -> return BracketExprImpl(node)
			type === BREAK_STMT -> return BreakStmtImpl(node)
			type === CALL_EXPR -> return CallExprImpl(node)
			type === CASE_EXPR -> return CaseExprImpl(node)
			type === CASE_STMT -> return CaseStmtImpl(node)
			type === CAST_EXPR -> return CastExprImpl(node)
			type === COMMAND_EXPR -> return CommandExprImpl(node)
			type === CONST_DEF -> return ConstDefImpl(node)
			type === CONST_SECT -> return ConstSectImpl(node)
			type === CONTINUE_STMT -> return ContinueStmtImpl(node)
			type === CTOR_ARG -> return CtorArgImpl(node)
			type === DISCARD_STMT -> return DiscardStmtImpl(node)
			type === DISTINCT_TYPE_CLASS -> return DistinctTypeClassImpl(node)
			type === DISTINCT_TYPE_EXPR -> return DistinctTypeExprImpl(node)
			type === DOT_EXPR -> return DotExprImpl(node)
			type === DO_BLOCK -> return DoBlockImpl(node)
			type === ENUM_DEF -> return EnumDefImpl(node)
			type === ENUM_MEMBER -> return EnumMemberImpl(node)
			type === ENUM_TYPE_CLASS -> return EnumTypeClassImpl(node)
			type === EXPR_STMT -> return ExprStmtImpl(node)
			type === FOR_STMT -> return ForStmtImpl(node)
			type === FROM_STMT -> return FromStmtImpl(node)
			type === GENERIC_PARAM -> return GenericParamImpl(node)
			type === GROUPED_EXPR -> return GroupedExprImpl(node)
			type === IDENTIFIER -> return IdentifierImpl(node)
			type === IDENTIFIER_DEF -> return IdentifierDefImpl(node)
			type === IDENTIFIER_DEFS -> return IdentifierDefsImpl(node)
			type === IDENTIFIER_EXPR -> return IdentifierExprImpl(node)
			type === IDENT_PRAGMA_PAIR -> return IdentPragmaPairImpl(node)
			type === IF_EXPR -> return IfExprImpl(node)
			type === IF_STMT -> return IfStmtImpl(node)
			type === INFIX_EXPR -> return InfixExprImpl(node)
			type === IMPORT_STMT -> return ImportStmtImpl(node)
			type === INCLUDE_STMT -> return IncludeStmtImpl(node)
			type === ITERATOR_DEF -> return IteratorDefImpl(node)
			type === LET_SECT -> return LetSectImpl(node)
			type === LITERAL -> return LiteralImpl(node)
			type === MACRO_DEF -> return MacroDefImpl(node)
			type === MIXIN_STMT -> return MixinStmtImpl(node)
			type === NIL_TOKEN -> return NilTokenImpl(node)
			type === OBJECT_CTOR -> return ObjectCtorImpl(node)
			type === OBJECT_DEF -> return ObjectDefImpl(node)
			type === OBJECT_FIELDS -> return ObjectFieldsImpl(node)
			type === OBJECT_TYPE_CLASS -> return ObjectTypeClassImpl(node)
			type === PATTERN -> return PatternImpl(node)
			type === PRAGMA -> return PragmaImpl(node)
			type === PRAGMA_STMT -> return PragmaStmtImpl(node)
			type === PREFIX_EXPR -> return PrefixExprImpl(node)
			type === PROC_DEF -> return ProcDefImpl(node)
			type === PROC_EXPR -> return ProcExprImpl(node)
			type === PROC_TYPE_CLASS -> return ProcTypeClassImpl(node)
			type === PROC_TYPE_EXPR -> return ProcTypeExprImpl(node)
			type === PTR_TYPE_CLASS -> return PtrTypeClassImpl(node)
			type === PTR_TYPE_EXPR -> return PtrTypeExprImpl(node)
			type === RAISE_STMT -> return RaiseStmtImpl(node)
			type === REF_TYPE_CLASS -> return RefTypeClassImpl(node)
			type === REF_TYPE_EXPR -> return RefTypeExprImpl(node)
			type === RETURN_STMT -> return ReturnStmtImpl(node)
			type === SET_OR_TABLE_CTOR -> return SetOrTableCtorImpl(node)
			type === STATIC_STMT -> return StaticStmtImpl(node)
			type === STATIC_TYPE_EXPR -> return StaticTypeExprImpl(node)
			type === STMT_LIST_EXPR -> return StmtListExprImpl(node)
			type === TEMPLATE_DEF -> return TemplateDefImpl(node)
			type === TRY_STMT -> return TryStmtImpl(node)
			type === TUPLE_CTOR -> return TupleCtorImpl(node)
			type === TUPLE_DEF -> return TupleDefImpl(node)
			type === TUPLE_TYPE_CLASS -> return TupleTypeClassImpl(node)
			type === TUPLE_TYPE_EXPR -> return TupleTypeExprImpl(node)
			type === TYPE_DEF -> return TypeDefImpl(node)
			type === TYPE_DESC -> return TypeDescImpl(node)
			type === TYPE_SECT -> return TypeSectImpl(node)
			type === VAR_DEF -> return VarDefImpl(node)
			type === VAR_SECT -> return VarSectImpl(node)
			type === VAR_TYPE_CLASS -> return VarTypeClassImpl(node)
			type === VAR_TYPE_EXPR -> return VarTypeExprImpl(node)
			type === WHEN_EXPR -> return WhenExprImpl(node)
			type === WHEN_STMT -> return WhenStmtImpl(node)
			type === WHILE_STMT -> return WhileStmtImpl(node)
			type === YIELD_STMT -> return YieldStmtImpl(node)
			type === METHOD_DEF -> return MethodDefImpl(node)
			type === EXPR_LIST -> return ExprListImpl(node)
			type === ROUTINE_PARAM_LIST -> return RoutineParamListImpl(node)
			type === CASE_BRANCH -> return CaseBranchImpl(node)
			type === ELSE_BRANCH -> return ElseBranchImpl(node)
			type === ELIF_BRANCH -> return ElifBranchImpl(node)
			type === BRACE_EXPR -> return BraceExprImpl(node)
			type === EXPR_COLON_EQ_EXPR_LIST -> return ExprColonEqExprListImpl(node)
			type === ELSE_STMT_BRANCH -> return ElseStmtBranchImpl(node)
			type === IDENT_COLON_EQUALS_WITH_PRAGMA -> return IdentColonEqualsWithPragmaImpl(node)
			type === ROUTINE_IDENTIFIER -> return RoutineIdentifierImpl(node)
			else -> throw AssertionError("Unknown element type: " + type)
		}
	}

	fun createIdentNode(project: Project, identifier: String): ASTNode {
		val file = createFile(project, identifier)
		val statements = file.getStatements()
		if (statements.size == 1) {
			val statement = statements[0]
			if (statement is ExprStmt) {
				val expression = PsiTreeUtil.getChildrenOfTypeAsList(statement, Expression::class.java)[0]
				if (expression is IdentifierExpr) {
					return expression.identifier.node.findChildByType(IDENT)!!
				}
			}
		}
		throw IncorrectOperationException("Invalid identifier")
	}

	private fun createFile(project: Project, source: String): NimFile {
		return PsiFileFactory.getInstance(project).createFileFromText(NimLanguage, source) as NimFile
	}

}
