NimJet - intellij nim-lang plugin
==

- IN EARLY DEVELOPMENT
- heavily based on https://bitbucket.org/dmitri_gb/idea-nim/
- my excersices with Kotlin

Build
==

Open in `idea-community` >= 173 with activated `Kotlin` plugin and build it. 

	TokenSet EXPRESSIONS = TokenSet.create(CALL_EXPR, ASSIGNMENT_EXPR, PREFIX_EXPR, COMMAND_EXPR, CASE_EXPR, PROC_EXPR, STMT_LIST_EXPR,
		WHEN_STMT, INFIX_EXPR, IDENTIFIER_EXPR, VAR_TYPE_EXPR);

	TokenSet STATEMENTS = TokenSet.create(IF_STMT, WHILE_STMT, EXPR_STMT, BLOCK_STMT, TRY_STMT, FOR_STMT, CASE_STMT,
		RETURN_STMT, WHEN_STMT);

	TokenSet SECTIONS = TokenSet.create(CONST_SECT, LET_SECT, VAR_SECT, TYPE_SECT, DO_BLOCK, TYPE_DESC, CASE_BRANCH);

	TokenSet DEFINITIONS = TokenSet.create(OBJECT_FIELDS, TYPE_DEF, OBJECT_DEF, PROC_DEF, VAR_DEF, ITERATOR_DEF, METHOD_DEF, TEMPLATE_DEF,
		MACRO_DEF, ENUM_DEF, TUPLE_DEF);

	TokenSet TOKENS = TokenSet.create(T_OF, BRACKET_CTOR);

	TokenSet BLOCK_START_TOKENS = TokenSet.orSet(EXPRESSIONS, STATEMENTS, SECTIONS, DEFINITIONS, TOKENS);

	// avoid extra indentation for these elements
	TokenSet AVOID_INDENT_TOKENS = TokenSet.create(T_ELIF, T_ELSE, T_EXCEPT, T_FINALLY, T_RPAREN/*for parenthesess expression*/,
		T_RBRACKET/*for ctor expression*/, DO_BLOCK);
