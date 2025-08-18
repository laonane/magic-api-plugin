package com.magicapi.idea.lang.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.magicapi.idea.lang.psi.MSTypes;
import com.intellij.psi.TokenType;

%%

%class MagicScriptLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{
  return;
%eof}

// 定义字符类
CRLF = \R
WHITE_SPACE = [\ \n\t\f]
FIRST_VALUE_CHARACTER = [^ \n\f\\] | "\\"{CRLF} | "\\".
VALUE_CHARACTER = [^\n\f\\] | "\\"{CRLF} | "\\".

// 标识符和数字
IDENTIFIER = [a-zA-Z_][a-zA-Z0-9_]*
NUMBER = [0-9]+(\.[0-9]*)?([eE][+-]?[0-9]+)?
INTEGER = [0-9]+

// 字符串字面量
STRING_LITERAL = \"([^\"\\\\]|\\\\.)*\"|\'([^\'\\\\]|\\\\.)*\'

// 注释
LINE_COMMENT = "//"[^\r\n]*
BLOCK_COMMENT = "/*"([^*]|"*"[^/])*"*/"

%state WAITING_VALUE

%%

<YYINITIAL> {
    // 关键字
    "var"                { return MSTypes.VAR; }
    "function"           { return MSTypes.FUNCTION; }
    "return"             { return MSTypes.RETURN; }
    "if"                 { return MSTypes.IF; }
    "else"               { return MSTypes.ELSE; }
    "for"                { return MSTypes.FOR; }
    "while"              { return MSTypes.WHILE; }
    "do"                 { return MSTypes.DO; }
    "break"              { return MSTypes.BREAK; }
    "continue"           { return MSTypes.CONTINUE; }
    "try"                { return MSTypes.TRY; }
    "catch"              { return MSTypes.CATCH; }
    "finally"            { return MSTypes.FINALLY; }
    "throw"              { return MSTypes.THROW; }
    "import"             { return MSTypes.IMPORT; }
    "export"             { return MSTypes.EXPORT; }
    "true"               { return MSTypes.TRUE; }
    "false"              { return MSTypes.FALSE; }
    "null"               { return MSTypes.NULL; }
    "undefined"          { return MSTypes.UNDEFINED; }

    // 内置模块关键字
    "db"                 { return MSTypes.BUILTIN_MODULE; }
    "http"               { return MSTypes.BUILTIN_MODULE; }
    "request"            { return MSTypes.BUILTIN_MODULE; }
    "response"           { return MSTypes.BUILTIN_MODULE; }
    "env"                { return MSTypes.BUILTIN_MODULE; }
    "log"                { return MSTypes.BUILTIN_MODULE; }

    // 操作符
    "+"                  { return MSTypes.PLUS; }
    "-"                  { return MSTypes.MINUS; }
    "*"                  { return MSTypes.MULTIPLY; }
    "/"                  { return MSTypes.DIVIDE; }
    "%"                  { return MSTypes.MODULO; }
    "="                  { return MSTypes.ASSIGN; }
    "+="                 { return MSTypes.PLUS_ASSIGN; }
    "-="                 { return MSTypes.MINUS_ASSIGN; }
    "*="                 { return MSTypes.MULTIPLY_ASSIGN; }
    "/="                 { return MSTypes.DIVIDE_ASSIGN; }
    "=="                 { return MSTypes.EQ; }
    "!="                 { return MSTypes.NE; }
    "<"                  { return MSTypes.LT; }
    ">"                  { return MSTypes.GT; }
    "<="                 { return MSTypes.LE; }
    ">="                 { return MSTypes.GE; }
    "&&"                 { return MSTypes.AND; }
    "||"                 { return MSTypes.OR; }
    "!"                  { return MSTypes.NOT; }
    "++"                 { return MSTypes.INCREMENT; }
    "--"                 { return MSTypes.DECREMENT; }

    // 分隔符
    "("                  { return MSTypes.LPAREN; }
    ")"                  { return MSTypes.RPAREN; }
    "{"                  { return MSTypes.LBRACE; }
    "}"                  { return MSTypes.RBRACE; }
    "["                  { return MSTypes.LBRACKET; }
    "]"                  { return MSTypes.RBRACKET; }
    ";"                  { return MSTypes.SEMICOLON; }
    ","                  { return MSTypes.COMMA; }
    "."                  { return MSTypes.DOT; }
    ":"                  { return MSTypes.COLON; }
    "?"                  { return MSTypes.QUESTION; }

    // 字面量
    {STRING_LITERAL}     { return MSTypes.STRING_LITERAL; }
    {NUMBER}             { return MSTypes.NUMBER_LITERAL; }
    {INTEGER}            { return MSTypes.INTEGER_LITERAL; }

    // 标识符
    {IDENTIFIER}         { return MSTypes.IDENTIFIER; }

    // 注释
    {LINE_COMMENT}       { return MSTypes.LINE_COMMENT; }
    {BLOCK_COMMENT}      { return MSTypes.BLOCK_COMMENT; }

    // 空白符
    {WHITE_SPACE}+       { return TokenType.WHITE_SPACE; }

    // 其他字符
    [^]                  { return TokenType.BAD_CHARACTER; }
}