package org.nimjet.parser;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;

import org.nimjet.psi.ElementTypes;
import com.intellij.psi.TokenType;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;
//import static org.nimjet.psi.ElementTypes.*;


%%

%{
  public NimLexer() {
    this((java.io.Reader)null);
  }
%}


//%unicode
//%class NimLexer
//%implements FlexLexer
//%public
//%function advance
//%type IElementType
//%eof{
//  return;
//%eof}


%unicode
%class NimLexer
%implements FlexLexer
%public
%function advance
%type IElementType
%eof{  return;
%eof}

CRLF=\n|\r|\r\n
WHITE_SPACE=[\ \t\f]
END_OF_LINE_COMMENT="#"[^\r\n]*
KEYWORD="echo"
T_VAR="var"
T_LET="let"
T_CONST="const"
T_SEQ="seq"
T_PROC="proc"
T_IMPORT="import"
T_ELIF="elif"
T_IF="if"
T_ELSE="else"
T_FOR="for"
T_IN="in"
T_WHILE="while"
T_BLOCK="block"
T_DISCARD="discard"
T_RETURN="return"
STRING=\"([^\"\\]|\\\\|\\\"|\\n|\\t)*\"
CHAR='.'
NUMBER=[0-9]+(\.[0-9]+)?
IDENTIFIER=[a-zA-Z][a-zA-Z_0-9]*
OPERATOR_IDENTIFIER=`[\=\+\-\*\/\<\>\@\$\~\&\%\|\!\?\^\.\:\\]+`
CUSTOM_OPERATOR=[\=\+\-\*\/\<\>\@\$\~\&\%\|\!\?\^\.\:\\]+
T_AT="@"
NIL="nil"
TEMPLATE=\{\.[a-zA-Z][a-zA-Z_0-9]*\.\}
OP_ASSIGN="="
OP_MOD="mod"
COLON=":"
COMMA=","
T_RANGE=".."
T_DOT="."
OPEN_BRACKET="("
CLOSE_BRACKET=")"
OPEN_SBRACKET="["
CLOSE_SBRACKET="]"

%state YYINITIAL

%%

<YYINITIAL> {
    {END_OF_LINE_COMMENT} { return ElementTypes.COMMENT; }
    {TEMPLATE}            { return ElementTypes.COMMENT; }
    {KEYWORD}             { return ElementTypes.KEYWORD; }
    {CRLF}+               { return TokenType.WHITE_SPACE; }
    {WHITE_SPACE}+        { return TokenType.WHITE_SPACE; }
    {T_IMPORT}            { return ElementTypes.T_IMPORT; }
    {T_VAR}               { return ElementTypes.T_VAR; }
    {T_LET}               { return ElementTypes.T_LET; }
    {T_CONST}             { return ElementTypes.T_CONST; }
    {T_SEQ}               { return ElementTypes.T_SEQ; }
    {T_PROC}              { return ElementTypes.T_PROC; }
    {T_ELIF}              { return ElementTypes.T_ELIF; }
    {T_IF}                { return ElementTypes.T_IF; }
    {T_ELSE}              { return ElementTypes.T_ELSE; }
    {T_FOR}               { return ElementTypes.T_FOR; }
    {T_IN}                { return ElementTypes.T_IN; }
    {T_WHILE}             { return ElementTypes.T_WHILE; }
    {T_AT}                { return ElementTypes.T_AT; }
    "true"                { return ElementTypes.T_TRUE; }
    "false"               { return ElementTypes.T_FALSE; }
    "case"                { return ElementTypes.T_CASE; }
    "of"                  { return ElementTypes.T_OF; }
    "break"               { return ElementTypes.T_BREAK; }
    "addr"                { return ElementTypes.OP_ADDR; }
    {T_BLOCK}             { return ElementTypes.T_BLOCK; }
    {T_DISCARD}           { return ElementTypes.T_DISCARD; }
    {STRING}              { return ElementTypes.STRING; }
    {CHAR}                { return ElementTypes.CHAR; }
    {NUMBER}              { return ElementTypes.NUMBER; }
    {T_RANGE}             { return ElementTypes.T_RANGE; }
    {T_DOT}               { return ElementTypes.T_DOT; }
    "&&"                  { return ElementTypes.OP_LOGICAL_AND; }
    "||"                  { return ElementTypes.OP_LOGICAL_OR; }
    {OP_MOD}              { return ElementTypes.OP_MOD; }
    "&"                   { return ElementTypes.OP_AND; }
    "and"                 { return ElementTypes.OP_AND_FULL; }
    "|"                   { return ElementTypes.OP_OR; }
    "or"                  { return ElementTypes.OP_OR_FULL; }
    "^"                   { return ElementTypes.OP_XOR; }
    "xor"                 { return ElementTypes.OP_XOR_FULL; }
    {NIL}                 { return ElementTypes.NIL; }
    {T_RETURN}            { return ElementTypes.T_RETURN; }
    {OPERATOR_IDENTIFIER} { return ElementTypes.OPERATOR_IDENTIFIER; }
    {IDENTIFIER}          { return ElementTypes.IDENTIFIER; }
    {OP_ASSIGN}           { return ElementTypes.OP_ASSIGN; }
    {COLON}               { return ElementTypes.COLON; }
    {COMMA}               { return ElementTypes.COMMA; }
    {OPEN_BRACKET}        { return ElementTypes.OPEN_BRACKET; }
    {CLOSE_BRACKET}       { return ElementTypes.CLOSE_BRACKET; }
    {OPEN_SBRACKET}       { return ElementTypes.OPEN_SBRACKET; }
    {CLOSE_SBRACKET}      { return ElementTypes.CLOSE_SBRACKET; }
    "=="                  { return ElementTypes.OP_EQ; }
    "!="                  { return ElementTypes.OP_NE; }
    "<"                   { return ElementTypes.OP_LT; }
    "<="                  { return ElementTypes.OP_LE; }
    ">"                   { return ElementTypes.OP_GT; }
    ">="                  { return ElementTypes.OP_GE; }
    "+"                   { return ElementTypes.OP_PLUS; }
    "-"                   { return ElementTypes.OP_MINUS; }
    "*"                   { return ElementTypes.OP_MULTIPLY; }
    "/"                   { return ElementTypes.OP_DIVIDE; }
    "$"                   { return ElementTypes.OP_STRINGIFY; }
    {CUSTOM_OPERATOR}     { return ElementTypes.CUSTOM_OPERATOR; }

    "when"                { return ElementTypes.T_WHEN; }
    "try"                 { return ElementTypes.T_TRY; }
    "except"              { return ElementTypes.T_EXCEPT; }
    "finally"             { return ElementTypes.T_FINALLY; }
    "include"             { return ElementTypes.T_INCLUDE; }
    "mixin"               { return ElementTypes.T_MIXIN; }
    .                     { return TokenType.WHITE_SPACE; }
}

