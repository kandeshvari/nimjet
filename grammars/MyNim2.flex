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
%eof{
  return;
%eof}

%{
  int blockCommentNestingLevel = 0;
%}


//EOL=\R
WHITE_SPACE = [ \t\n\x0B\f\r]+

//CRLF = \n
//SPACE=[ \t\n\x0B\f\r]+
ID = ([:letter:] | [\u2013]) ("_"? ([:letter:] | [:digit:] | [\u2013]))*
//STRING=('([^'\\]|\\.)*'|\"([^\"\\]|\\\"|\\'|\\)*\")
//NUMBER=[0-9]+(\.[0-9]+)*
//CHAR=[a-zA-Z]
LINE_COMMENT = "#" [^\r\n]* (\r? \n [\ \t]* "#" [^\r\n]*)*
//BLOCK_COMMENT=#\[(.|\n)*\]#


HEX_LIT = "0" [xX] [0-9A-Fa-f] [0-9A-Fa-f_]*
DEC_LIT = [0-9] [0-9_]*
OCT_LIT = "0" [ocC] [0-7] [0-7_]*
BIN_LIT = "0" [bB] [01] [01_]*

EXPONENT = [Ee] [-+]? [0-9] [0-9_]*
FLOAT_LITERAL   = [0-9] [0-9_]* (("." [0-9_]+ {EXPONENT}?) | {EXPONENT})
FLOAT32_SUFFIX = [fF] "32"?
FLOAT32_LITERAL = {HEX_LIT} \' {FLOAT32_SUFFIX}
                | ({FLOAT_LITERAL} | {DEC_LIT} | {OCT_LIT} | {BIN_LIT}) \'? {FLOAT32_SUFFIX}
FLOAT64_SUFFIX = [fF] "64" | [dD]
FLOAT64_LITERAL = {HEX_LIT} \' {FLOAT64_SUFFIX}
                | ({FLOAT_LITERAL} | {DEC_LIT} | {OCT_LIT} | {BIN_LIT}) \'? {FLOAT64_SUFFIX}

CHARACTER_LITERAL = \' (\\. | [^\\\'\r\n])* \'?
STRING_LITERAL = \" (\\. | [^\\\"\r\n])* \"?
TRIPLESTR_LITERAL = \"\"\" ([^\"]+ | \"\"? [^\"]+)* \"*\"\"\"


INT_LITERAL     = {HEX_LIT} | {DEC_LIT} | {OCT_LIT} | {BIN_LIT}
INT8_LITERAL    = {INT_LITERAL} \'? [iI] "8"
INT16_LITERAL   = {INT_LITERAL} \'? [iI] "16"
INT32_LITERAL   = {INT_LITERAL} \'? [iI] "32"
INT64_LITERAL   = {INT_LITERAL} \'? [iI] "64"
UINT_LITERAL    = {INT_LITERAL} \'? [uU]
UINT8_LITERAL   = {UINT_LITERAL} "8"
UINT16_LITERAL  = {UINT_LITERAL} "16"
UINT32_LITERAL  = {UINT_LITERAL} "32"
UINT64_LITERAL  = {UINT_LITERAL} "64"



%state ST_BLOCK_COMMENT

%%
<YYINITIAL> {
  {WHITE_SPACE}        { return WHITE_SPACE; }

  "#[" { blockCommentNestingLevel = 1; yybegin(ST_BLOCK_COMMENT); return BLOCK_COMMENT; }

  "int"                { return T_TYPE_INT; }
  "string"             { return T_TYPE_STRING; }
  "float"              { return T_TYPE_FLOAT; }
  "proc"               { return T_PROC; }
  "var"                { return T_VAR; }
  "let"                { return T_LET; }
  "const"              { return T_CONST; }
  "import"             { return T_IMPORT; }
  "do"                 { return T_DO; }
  "echo"               { return T_ECHO; }
  "="                  { return T_EQ; }
  ":"                  { return T_COLON; }
  "("                  { return T_OPEN_BRACKET; }
  ")"                  { return T_CLOSE_BRACKET; }
  "["                  { return T_OPEN_SBRACKET; }
  "]"                  { return T_CLOSE_SBRACKET; }
  ","                  { return T_COMMA; }
  ";"                  { return T_SEMICOLON; }
  {LINE_COMMENT}            { return COMMENT; }
  "INDGT"              { return INDGT; }
  "INDEQ"              { return INDEQ; }
  "indNone"            { return INDNONE; }
  "nil"                { return T_NIL; }
  {FLOAT64_LITERAL}     { return FLOAT64_LITERAL; }
  {FLOAT32_LITERAL}     { return FLOAT32_LITERAL; }
  {FLOAT_LITERAL}   { return FLOAT_LITERAL; }
  {UINT64_LITERAL}  { return UINT64_LITERAL; }
  {UINT32_LITERAL}  { return UINT32_LITERAL; }
  {UINT16_LITERAL}  { return UINT16_LITERAL; }
  {UINT8_LITERAL}   { return UINT8_LITERAL; }
  {UINT_LITERAL}    { return UINT_LITERAL; }
  {INT64_LITERAL}   { return INT64_LITERAL; }
  {INT32_LITERAL}   { return INT32_LITERAL; }
  {INT16_LITERAL}   { return INT16_LITERAL; }
  {INT8_LITERAL}    { return INT8_LITERAL; }
  {INT_LITERAL}     { return INT_LITERAL; }
  {STRING_LITERAL}           { return RSTR_LITERAL; }
  {TRIPLESTR_LITERAL}      { return TRIPLESTR_LITERAL; }
  {CHARACTER_LITERAL}      { return CHAR_LITERAL; }

//  {CRLF}               { return CRLF; }
//  {SPACE}              { return SPACE; }
  {ID}                 { return ID; }
//  {STRING}             { return STRING; }
//  {NUMBER}             { return NUMBER; }
//  {CHAR}               { return CHAR; }
//  {LINE_COMMENT}       { return LINE_COMMENT; }
//  {BLOCK_COMMENT}      { return BLOCK_COMMENT; }

[^] { return BAD_CHARACTER; }
}

<ST_BLOCK_COMMENT> {
  "#[" { blockCommentNestingLevel++; return BLOCK_COMMENT; }
  "]#" {
      if (--blockCommentNestingLevel == 0) yybegin(YYINITIAL);
      return BLOCK_COMMENT;
  }
  [^] { return BLOCK_COMMENT; }
}
