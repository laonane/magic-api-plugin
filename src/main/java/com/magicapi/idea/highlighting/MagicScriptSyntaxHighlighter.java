package com.magicapi.idea.highlighting;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import com.magicapi.idea.lang.lexer.MagicScriptLexerAdapter;
import com.magicapi.idea.lang.psi.MSTypes;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class MagicScriptSyntaxHighlighter extends SyntaxHighlighterBase {
    
    // 定义高亮样式
    private static final TextAttributesKey KEYWORD = TextAttributesKey.createTextAttributesKey(
            "MAGIC_SCRIPT_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);
    private static final TextAttributesKey STRING = TextAttributesKey.createTextAttributesKey(
            "MAGIC_SCRIPT_STRING", DefaultLanguageHighlighterColors.STRING);
    private static final TextAttributesKey NUMBER = TextAttributesKey.createTextAttributesKey(
            "MAGIC_SCRIPT_NUMBER", DefaultLanguageHighlighterColors.NUMBER);
    private static final TextAttributesKey COMMENT = TextAttributesKey.createTextAttributesKey(
            "MAGIC_SCRIPT_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
    private static final TextAttributesKey OPERATOR = TextAttributesKey.createTextAttributesKey(
            "MAGIC_SCRIPT_OPERATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN);
    private static final TextAttributesKey IDENTIFIER = TextAttributesKey.createTextAttributesKey(
            "MAGIC_SCRIPT_IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER);
    private static final TextAttributesKey BUILTIN_MODULE = TextAttributesKey.createTextAttributesKey(
            "MAGIC_SCRIPT_BUILTIN_MODULE", DefaultLanguageHighlighterColors.PREDEFINED_SYMBOL);
    
    private static final Map<IElementType, TextAttributesKey> ATTRIBUTES = new HashMap<>();
    
    static {
        // 关键字
        ATTRIBUTES.put(MSTypes.VAR, KEYWORD);
        ATTRIBUTES.put(MSTypes.FUNCTION, KEYWORD);
        ATTRIBUTES.put(MSTypes.RETURN, KEYWORD);
        ATTRIBUTES.put(MSTypes.IF, KEYWORD);
        ATTRIBUTES.put(MSTypes.ELSE, KEYWORD);
        ATTRIBUTES.put(MSTypes.FOR, KEYWORD);
        ATTRIBUTES.put(MSTypes.WHILE, KEYWORD);
        ATTRIBUTES.put(MSTypes.DO, KEYWORD);
        ATTRIBUTES.put(MSTypes.BREAK, KEYWORD);
        ATTRIBUTES.put(MSTypes.CONTINUE, KEYWORD);
        ATTRIBUTES.put(MSTypes.TRY, KEYWORD);
        ATTRIBUTES.put(MSTypes.CATCH, KEYWORD);
        ATTRIBUTES.put(MSTypes.FINALLY, KEYWORD);
        ATTRIBUTES.put(MSTypes.THROW, KEYWORD);
        ATTRIBUTES.put(MSTypes.IMPORT, KEYWORD);
        ATTRIBUTES.put(MSTypes.EXPORT, KEYWORD);
        ATTRIBUTES.put(MSTypes.TRUE, KEYWORD);
        ATTRIBUTES.put(MSTypes.FALSE, KEYWORD);
        ATTRIBUTES.put(MSTypes.NULL, KEYWORD);
        ATTRIBUTES.put(MSTypes.UNDEFINED, KEYWORD);
        
        // 字面量
        ATTRIBUTES.put(MSTypes.STRING_LITERAL, STRING);
        ATTRIBUTES.put(MSTypes.NUMBER_LITERAL, NUMBER);
        ATTRIBUTES.put(MSTypes.INTEGER_LITERAL, NUMBER);
        
        // 注释  
        ATTRIBUTES.put(MSTypes.LINE_COMMENT, COMMENT);
        ATTRIBUTES.put(MSTypes.BLOCK_COMMENT, COMMENT);
        
        // 内置模块
        ATTRIBUTES.put(MSTypes.BUILTIN_MODULE, BUILTIN_MODULE);
        
        // 操作符
        ATTRIBUTES.put(MSTypes.PLUS, OPERATOR);
        ATTRIBUTES.put(MSTypes.MINUS, OPERATOR);
        ATTRIBUTES.put(MSTypes.MULTIPLY, OPERATOR);
        ATTRIBUTES.put(MSTypes.DIVIDE, OPERATOR);
        ATTRIBUTES.put(MSTypes.ASSIGN, OPERATOR);
        ATTRIBUTES.put(MSTypes.EQ, OPERATOR);
        ATTRIBUTES.put(MSTypes.NE, OPERATOR);
        ATTRIBUTES.put(MSTypes.LT, OPERATOR);
        ATTRIBUTES.put(MSTypes.GT, OPERATOR);
        ATTRIBUTES.put(MSTypes.LE, OPERATOR);
        ATTRIBUTES.put(MSTypes.GE, OPERATOR);
        ATTRIBUTES.put(MSTypes.AND, OPERATOR);
        ATTRIBUTES.put(MSTypes.OR, OPERATOR);
        ATTRIBUTES.put(MSTypes.NOT, OPERATOR);
        
        // 标识符
        ATTRIBUTES.put(MSTypes.IDENTIFIER, IDENTIFIER);
    }
    
    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new MagicScriptLexerAdapter();
    }
    
    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        TextAttributesKey key = ATTRIBUTES.get(tokenType);
        return key != null ? new TextAttributesKey[]{key} : new TextAttributesKey[0];
    }
}