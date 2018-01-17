import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import generated.psi.impl.*
import org.nimjet.psi.ElementTypes.*

object ElementFactory {
	fun createElement(node: ASTNode): PsiElement {
		val type = node.elementType
		if (type === ASM_STMT) {
			return AsmStmtImpl(node)
		} else if (type === ASSIGNMENT_EXPR) {
			return AssignmentExprImpl(node)
		} else if (type === BIND_STMT) {
			return BindStmtImpl(node)
		} else if (type === BLOCK) {
			return BlockImpl(node)
		} else if (type === BLOCK_STMT) {
			return BlockStmtImpl(node)
		} else if (type === BRACKET_CTOR) {
			return BracketCtorImpl(node)
		} else if (type === BRACKET_EXPR) {
			return BracketExprImpl(node)
		} else if (type === BREAK_STMT) {
			return BreakStmtImpl(node)
		} else if (type === CALL_EXPR) {
			return CallExprImpl(node)
		} else if (type === CASE_EXPR) {
			return CaseExprImpl(node)
		} else if (type === CASE_STMT) {
			return CaseStmtImpl(node)
		} else if (type === CAST_EXPR) {
			return CastExprImpl(node)
		} else if (type === COMMAND_EXPR) {
			return CommandExprImpl(node)
		} else if (type === CONST_DEF) {
			return ConstDefImpl(node)
		} else if (type === CONST_SECT) {
			return ConstSectImpl(node)
		} else if (type === CONTINUE_STMT) {
			return ContinueStmtImpl(node)
		} else if (type === CTOR_ARG) {
			return CtorArgImpl(node)
		} else if (type === DISCARD_STMT) {
			return DiscardStmtImpl(node)
		} else if (type === DISTINCT_TYPE_CLASS) {
			return DistinctTypeClassImpl(node)
		} else if (type === DISTINCT_TYPE_EXPR) {
			return DistinctTypeExprImpl(node)
		} else if (type === DOT_EXPR) {
			return DotExprImpl(node)
		} else if (type === DO_BLOCK) {
			return DoBlockImpl(node)
		} else if (type === ENUM_DEF) {
			return EnumDefImpl(node)
		} else if (type === ENUM_MEMBER) {
			return EnumMemberImpl(node)
		} else if (type === ENUM_TYPE_CLASS) {
			return EnumTypeClassImpl(node)
		} else if (type === EXPR_STMT) {
			return ExprStmtImpl(node)
		} else if (type === FOR_STMT) {
			return ForStmtImpl(node)
		} else if (type === FROM_STMT) {
			return FromStmtImpl(node)
		} else if (type === GENERIC_PARAM) {
			return GenericParamImpl(node)
		} else if (type === GROUPED_EXPR) {
			return GroupedExprImpl(node)
		} else if (type === IDENTIFIER) {
			return IdentifierImpl(node)
		} else if (type === IDENTIFIER_DEF) {
			return IdentifierDefImpl(node)
		} else if (type === IDENTIFIER_DEFS) {
			return IdentifierDefsImpl(node)
		} else if (type === IDENTIFIER_EXPR) {
			return IdentifierExprImpl(node)
		} else if (type === IDENT_PRAGMA_PAIR) {
			return IdentPragmaPairImpl(node)
		} else if (type === IF_EXPR) {
			return IfExprImpl(node)
		} else if (type === IF_STMT) {
			return IfStmtImpl(node)
		} else if (type === INFIX_EXPR) {
			return InfixExprImpl(node)
		} else if (type === IMPORT_STMT) {
			return ImportStmtImpl(node)
		} else if (type === INCLUDE_STMT) {
			return IncludeStmtImpl(node)
		} else if (type === ITERATOR_DEF) {
			return IteratorDefImpl(node)
		} else if (type === LET_SECT) {
			return LetSectImpl(node)
		} else if (type === LITERAL) {
			return LiteralImpl(node)
		} else if (type === MACRO_DEF) {
			return MacroDefImpl(node)
		} else if (type === MIXIN_STMT) {
			return MixinStmtImpl(node)
		} else if (type === NIL_TOKEN) {
			return NilTokenImpl(node)
		} else if (type === OBJECT_CTOR) {
			return ObjectCtorImpl(node)
		} else if (type === OBJECT_DEF) {
			return ObjectDefImpl(node)
		} else if (type === OBJECT_FIELDS) {
			return ObjectFieldsImpl(node)
		} else if (type === OBJECT_TYPE_CLASS) {
			return ObjectTypeClassImpl(node)
		} else if (type === PATTERN) {
			return PatternImpl(node)
		} else if (type === PRAGMA) {
			return PragmaImpl(node)
		} else if (type === PRAGMA_STMT) {
			return PragmaStmtImpl(node)
		} else if (type === PREFIX_EXPR) {
			return PrefixExprImpl(node)
		} else if (type === PROC_DEF) {
			return ProcDefImpl(node)
		} else if (type === PROC_EXPR) {
			return ProcExprImpl(node)
		} else if (type === PROC_TYPE_CLASS) {
			return ProcTypeClassImpl(node)
		} else if (type === PROC_TYPE_EXPR) {
			return ProcTypeExprImpl(node)
		} else if (type === PTR_TYPE_CLASS) {
			return PtrTypeClassImpl(node)
		} else if (type === PTR_TYPE_EXPR) {
			return PtrTypeExprImpl(node)
		} else if (type === RAISE_STMT) {
			return RaiseStmtImpl(node)
		} else if (type === REF_TYPE_CLASS) {
			return RefTypeClassImpl(node)
		} else if (type === REF_TYPE_EXPR) {
			return RefTypeExprImpl(node)
		} else if (type === RETURN_STMT) {
			return ReturnStmtImpl(node)
		} else if (type === SET_OR_TABLE_CTOR) {
			return SetOrTableCtorImpl(node)
		} else if (type === STATIC_STMT) {
			return StaticStmtImpl(node)
		} else if (type === STATIC_TYPE_EXPR) {
			return StaticTypeExprImpl(node)
		} else if (type === STMT_LIST_EXPR) {
			return StmtListExprImpl(node)
		} else if (type === TEMPLATE_DEF) {
			return TemplateDefImpl(node)
		} else if (type === TRY_STMT) {
			return TryStmtImpl(node)
		} else if (type === TUPLE_CTOR) {
			return TupleCtorImpl(node)
		} else if (type === TUPLE_DEF) {
			return TupleDefImpl(node)
		} else if (type === TUPLE_TYPE_CLASS) {
			return TupleTypeClassImpl(node)
		} else if (type === TUPLE_TYPE_EXPR) {
			return TupleTypeExprImpl(node)
		} else if (type === TYPE_DEF) {
			return TypeDefImpl(node)
		} else if (type === TYPE_DESC) {
			return TypeDescImpl(node)
		} else if (type === TYPE_SECT) {
			return TypeSectImpl(node)
		} else if (type === VAR_DEF) {
			return VarDefImpl(node)
		} else if (type === VAR_SECT) {
			return VarSectImpl(node)
		} else if (type === VAR_TYPE_CLASS) {
			return VarTypeClassImpl(node)
		} else if (type === VAR_TYPE_EXPR) {
			return VarTypeExprImpl(node)
		} else if (type === WHEN_EXPR) {
			return WhenExprImpl(node)
		} else if (type === WHEN_STMT) {
			return WhenStmtImpl(node)
		} else if (type === WHILE_STMT) {
			return WhileStmtImpl(node)
		} else if (type === YIELD_STMT) {
			return YieldStmtImpl(node)
		}
		throw AssertionError("Unknown element type: " + type)
	}
}
