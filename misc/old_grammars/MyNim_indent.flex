package org.nimjet.parser;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;
import static org.nimjet.psi.ElementTypes.*;

%%

%{
  public _NimLexer() {
    this((java.io.Reader)null);
  }
%}

%public
%class _NimLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

EOL=\R
WHITE_SPACE=\s+

CHAR_LITERAL=.
HEX_LIT=0[xX][0-9A-Fa-f][0-9A-Fa-f_]*
DEC_LIT=[0-9][0-9_]*
OCT_LIT=0[ocC][0-7][0-7_]*
BIN_LIT=0[bB][01][01_]*
EXPONENT=[Ee][-+]?[0-9][0-9_]*
FLOAT_LITERAL=[0-9][0-9_]*((.[0-9_]+[Ee][-+]?[0-9][0-9_]*)|[Ee][-+]?[0-9][0-9_]*)
STRING_LITERAL=('([^'\\]|\\.)*'|\"([^\"\\]|\\\"|\\'|\\)*\")
SPACE=[ \t\n\x0B\f\r]+
ID=[:letter:][a-zA-Z_0-9]*
STRING=('([^'\\]|\\.)*'|\"([^\"\\]|\\\"|\\'|\\)*\")
NUMBER=[0-9]+(\.[0-9]+)*
LINE_COMMENT=#.*
BLOCK_COMMENT=#\[(.|\n)*\]#

%%
<YYINITIAL> {
  {WHITE_SPACE}          { return WHITE_SPACE; }

  "int"                  { return T_TYPE_INT; }
  "string"               { return T_TYPE_STRING; }
  "float"                { return T_TYPE_FLOAT; }
  "proc"                 { return T_PROC; }
  "var"                  { return T_VAR; }
  "let"                  { return T_LET; }
  "const"                { return T_CONST; }
  "import"               { return T_IMPORT; }
  "do"                   { return T_DO; }
  "nil"                  { return T_NIL; }
  "echo"                 { return T_ECHO; }
  "="                    { return T_EQ; }
  ":"                    { return T_COLON; }
  "."                    { return T_DOT; }
  "("                    { return T_OPEN_BRACKET; }
  ")"                    { return T_CLOSE_BRACKET; }
  "["                    { return T_OPEN_SBRACKET; }
  "]"                    { return T_CLOSE_SBRACKET; }
  ","                    { return T_COMMA; }
  ";"                    { return T_SEMICOLON; }
  "INT8_LITERAL"         { return INT8_LITERAL; }
  "INT16_LITERAL"        { return INT16_LITERAL; }
  "INT32_LITERAL"        { return INT32_LITERAL; }
  "INT64_LITERAL"        { return INT64_LITERAL; }
  "UINT_LITERAL"         { return UINT_LITERAL; }
  "UINT8_LITERAL"        { return UINT8_LITERAL; }
  "UINT16_LITERAL"       { return UINT16_LITERAL; }
  "UINT32_LITERAL"       { return UINT32_LITERAL; }
  "UINT64_LITERAL"       { return UINT64_LITERAL; }
  "FLOAT32_LITERAL"      { return FLOAT32_LITERAL; }
  "FLOAT64_LITERAL"      { return FLOAT64_LITERAL; }
  "RSTR_LITERAL"         { return RSTR_LITERAL; }
  "TRIPLESTR_LITERAL"    { return TRIPLESTR_LITERAL; }

  {CHAR_LITERAL}         { return CHAR_LITERAL; }
  {HEX_LIT}              { return HEX_LIT; }
  {DEC_LIT}              { return DEC_LIT; }
  {OCT_LIT}              { return OCT_LIT; }
  {BIN_LIT}              { return BIN_LIT; }
  {EXPONENT}             { return EXPONENT; }
  {FLOAT_LITERAL}        { return FLOAT_LITERAL; }
  {STRING_LITERAL}       { return STRING_LITERAL; }
  {SPACE}                { return SPACE; }
  {ID}                   { return ID; }
  {STRING}               { return STRING; }
  {NUMBER}               { return NUMBER; }
  {LINE_COMMENT}         { return LINE_COMMENT; }
  {BLOCK_COMMENT}        { return BLOCK_COMMENT; }

}

[^] { return BAD_CHARACTER; }
