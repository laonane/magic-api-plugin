package com.magicapi.idea.lang.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.magicapi.idea.lang.psi.MSTypes;

/**
 * Magic Script Lexer
 * 简化的词法分析器实现
 */
public class MagicScriptLexer implements FlexLexer {
    
    private CharSequence buffer;
    private int startOffset;
    private int endOffset;
    private int currentPosition;
    
    public MagicScriptLexer(java.io.Reader reader) {
        // 简化构造函数
    }
    
    @Override
    public void yybegin(int newState) {
        // 简化实现
    }
    
    @Override
    public int yystate() {
        return 0;
    }
    
    @Override
    public IElementType advance() throws java.io.IOException {
        if (currentPosition >= endOffset) {
            return null;
        }
        
        // 简化的token识别
        char currentChar = buffer.charAt(currentPosition);
        currentPosition++;
        
        if (Character.isLetter(currentChar) || currentChar == '_') {
            return MSTypes.IDENTIFIER;
        } else if (currentChar == '.') {
            return MSTypes.DOT;
        } else if (currentChar == '(') {
            return MSTypes.LPAREN;
        } else if (currentChar == ')') {
            return MSTypes.RPAREN;
        } else if (currentChar == ',') {
            return MSTypes.COMMA;
        } else if (Character.isWhitespace(currentChar)) {
            return com.intellij.psi.TokenType.WHITE_SPACE;
        }
        
        return MSTypes.IDENTIFIER;
    }
    
    @Override
    public void reset(CharSequence buffer, int start, int end, int initialState) {
        this.buffer = buffer;
        this.startOffset = start;
        this.endOffset = end;
        this.currentPosition = start;
    }
    
    @Override
    public int getTokenStart() {
        return currentPosition - 1;
    }
    
    @Override
    public int getTokenEnd() {
        return currentPosition;
    }
    
    public CharSequence yytext() {
        return buffer.subSequence(getTokenStart(), getTokenEnd());
    }
}