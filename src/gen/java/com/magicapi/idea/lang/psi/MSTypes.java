package com.magicapi.idea.lang.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.extapi.psi.ASTWrapperPsiElement;

public class MSTypes {
    // 关键字
    public static final IElementType VAR = new MSTokenType("VAR");
    public static final IElementType FUNCTION = new MSTokenType("FUNCTION");
    public static final IElementType RETURN = new MSTokenType("RETURN");
    public static final IElementType IF = new MSTokenType("IF");
    public static final IElementType ELSE = new MSTokenType("ELSE");
    public static final IElementType FOR = new MSTokenType("FOR");
    public static final IElementType WHILE = new MSTokenType("WHILE");
    public static final IElementType DO = new MSTokenType("DO");
    public static final IElementType BREAK = new MSTokenType("BREAK");
    public static final IElementType CONTINUE = new MSTokenType("CONTINUE");
    public static final IElementType TRY = new MSTokenType("TRY");
    public static final IElementType CATCH = new MSTokenType("CATCH");
    public static final IElementType FINALLY = new MSTokenType("FINALLY");
    public static final IElementType THROW = new MSTokenType("THROW");
    public static final IElementType IMPORT = new MSTokenType("IMPORT");
    public static final IElementType EXPORT = new MSTokenType("EXPORT");
    public static final IElementType TRUE = new MSTokenType("TRUE");
    public static final IElementType FALSE = new MSTokenType("FALSE");
    public static final IElementType NULL = new MSTokenType("NULL");
    public static final IElementType UNDEFINED = new MSTokenType("UNDEFINED");

    // 内置模块
    public static final IElementType BUILTIN_MODULE = new MSTokenType("BUILTIN_MODULE");

    // 操作符
    public static final IElementType PLUS = new MSTokenType("PLUS");
    public static final IElementType MINUS = new MSTokenType("MINUS");
    public static final IElementType MULTIPLY = new MSTokenType("MULTIPLY");
    public static final IElementType DIVIDE = new MSTokenType("DIVIDE");
    public static final IElementType MODULO = new MSTokenType("MODULO");
    public static final IElementType ASSIGN = new MSTokenType("ASSIGN");
    public static final IElementType PLUS_ASSIGN = new MSTokenType("PLUS_ASSIGN");
    public static final IElementType MINUS_ASSIGN = new MSTokenType("MINUS_ASSIGN");
    public static final IElementType MULTIPLY_ASSIGN = new MSTokenType("MULTIPLY_ASSIGN");
    public static final IElementType DIVIDE_ASSIGN = new MSTokenType("DIVIDE_ASSIGN");
    public static final IElementType EQ = new MSTokenType("EQ");
    public static final IElementType NE = new MSTokenType("NE");
    public static final IElementType LT = new MSTokenType("LT");
    public static final IElementType GT = new MSTokenType("GT");
    public static final IElementType LE = new MSTokenType("LE");
    public static final IElementType GE = new MSTokenType("GE");
    public static final IElementType AND = new MSTokenType("AND");
    public static final IElementType OR = new MSTokenType("OR");
    public static final IElementType NOT = new MSTokenType("NOT");
    public static final IElementType INCREMENT = new MSTokenType("INCREMENT");
    public static final IElementType DECREMENT = new MSTokenType("DECREMENT");

    // 分隔符
    public static final IElementType LPAREN = new MSTokenType("LPAREN");
    public static final IElementType RPAREN = new MSTokenType("RPAREN");
    public static final IElementType LBRACE = new MSTokenType("LBRACE");
    public static final IElementType RBRACE = new MSTokenType("RBRACE");
    public static final IElementType LBRACKET = new MSTokenType("LBRACKET");
    public static final IElementType RBRACKET = new MSTokenType("RBRACKET");
    public static final IElementType SEMICOLON = new MSTokenType("SEMICOLON");
    public static final IElementType COMMA = new MSTokenType("COMMA");
    public static final IElementType DOT = new MSTokenType("DOT");
    public static final IElementType COLON = new MSTokenType("COLON");
    public static final IElementType QUESTION = new MSTokenType("QUESTION");

    // 字面量
    public static final IElementType STRING_LITERAL = new MSTokenType("STRING_LITERAL");
    public static final IElementType NUMBER_LITERAL = new MSTokenType("NUMBER_LITERAL");
    public static final IElementType INTEGER_LITERAL = new MSTokenType("INTEGER_LITERAL");

    // 标识符
    public static final IElementType IDENTIFIER = new MSTokenType("IDENTIFIER");

    // 注释
    public static final IElementType LINE_COMMENT = new MSTokenType("LINE_COMMENT");
    public static final IElementType BLOCK_COMMENT = new MSTokenType("BLOCK_COMMENT");

    // 工厂方法
    public static class Factory {
        public static PsiElement createElement(ASTNode node) {
            return new ASTWrapperPsiElement(node);
        }
    }
}