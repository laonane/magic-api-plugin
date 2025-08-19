package com.magicapi.idea.lang.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

/**
 * Magic Script Token Types
 * 临时手动创建的类型定义，用于解决编译问题
 */
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
    public static final IElementType MUL = new MSTokenType("MUL");
    public static final IElementType DIVIDE = new MSTokenType("DIVIDE");
    public static final IElementType DIV = new MSTokenType("DIV");
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
    public static final IElementType NUMBER = new MSTokenType("NUMBER");
    public static final IElementType INTEGER_LITERAL = new MSTokenType("INTEGER_LITERAL");
    
    // 标识符
    public static final IElementType IDENTIFIER = new MSTokenType("IDENTIFIER");
    
    // 注释
    public static final IElementType LINE_COMMENT = new MSTokenType("LINE_COMMENT");
    public static final IElementType BLOCK_COMMENT = new MSTokenType("BLOCK_COMMENT");
    public static final IElementType COMMENT = new MSTokenType("COMMENT");
    
    // PSI Elements (语法结构元素)
    public static final IElementType MEMBER_ACCESS = new MSElementType("MEMBER_ACCESS");
    public static final IElementType FUNCTION_CALL = new MSElementType("FUNCTION_CALL");
    public static final IElementType PRIMARY_EXPRESSION = new MSElementType("PRIMARY_EXPRESSION");
    public static final IElementType POSTFIX_EXPRESSION = new MSElementType("POSTFIX_EXPRESSION");
    public static final IElementType VAR_DECLARATION = new MSElementType("VAR_DECLARATION");
    public static final IElementType FUNCTION_DECLARATION = new MSElementType("FUNCTION_DECLARATION");
    public static final IElementType EXPRESSION_STATEMENT = new MSElementType("EXPRESSION_STATEMENT");
    public static final IElementType ASSIGNMENT_EXPRESSION = new MSElementType("ASSIGNMENT_EXPRESSION");
    
    // 语句和块
    public static final IElementType BLOCK_STATEMENT = new MSElementType("BLOCK_STATEMENT");
    public static final IElementType FUNCTION_BODY = new MSElementType("FUNCTION_BODY");
    public static final IElementType IF_STATEMENT = new MSElementType("IF_STATEMENT");
    public static final IElementType FOR_STATEMENT = new MSElementType("FOR_STATEMENT");
    public static final IElementType WHILE_STATEMENT = new MSElementType("WHILE_STATEMENT");
    
    // Token Sets for highlighting
    public static final TokenSet KEYWORDS = TokenSet.create(
        VAR, FUNCTION, RETURN, IF, ELSE, FOR, WHILE, DO, BREAK, CONTINUE,
        TRY, CATCH, FINALLY, THROW, IMPORT, EXPORT, TRUE, FALSE, NULL, UNDEFINED
    );
    
    public static final TokenSet COMMENTS = TokenSet.create(LINE_COMMENT, BLOCK_COMMENT);
    
    public static final TokenSet LITERALS = TokenSet.create(
        STRING_LITERAL, NUMBER_LITERAL, INTEGER_LITERAL, TRUE, FALSE, NULL, UNDEFINED
    );
    
    public static final TokenSet OPERATORS = TokenSet.create(
        PLUS, MINUS, MULTIPLY, DIVIDE, MODULO, ASSIGN, PLUS_ASSIGN, MINUS_ASSIGN,
        MULTIPLY_ASSIGN, DIVIDE_ASSIGN, EQ, NE, LT, GT, LE, GE, AND, OR, NOT,
        INCREMENT, DECREMENT
    );
    
    public static final TokenSet SEPARATORS = TokenSet.create(
        LPAREN, RPAREN, LBRACE, RBRACE, LBRACKET, RBRACKET, SEMICOLON, COMMA, DOT, COLON, QUESTION
    );
    
    /**
     * Factory for creating PSI elements
     */
    public static class Factory {
        public static com.intellij.psi.PsiElement createElement(com.intellij.lang.ASTNode node) {
            // 简化实现，返回基础PsiElement包装
            return new com.intellij.extapi.psi.ASTWrapperPsiElement(node);
        }
    }
}