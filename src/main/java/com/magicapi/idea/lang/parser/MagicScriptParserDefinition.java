package com.magicapi.idea.lang.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.TokenType;
import com.magicapi.idea.lang.MagicScriptLanguage;
import com.magicapi.idea.lang.lexer.MagicScriptLexerAdapter;
import com.magicapi.idea.lang.psi.MSFile;
import com.magicapi.idea.lang.psi.MSTypes;
import org.jetbrains.annotations.NotNull;

public class MagicScriptParserDefinition implements ParserDefinition {
    public static final IFileElementType FILE = new IFileElementType(MagicScriptLanguage.INSTANCE);
    
    public static final TokenSet WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE);
    public static final TokenSet COMMENTS = TokenSet.create(MSTypes.LINE_COMMENT, MSTypes.BLOCK_COMMENT);
    public static final TokenSet STRINGS = TokenSet.create(MSTypes.STRING_LITERAL);
    public static final TokenSet KEYWORDS = TokenSet.create(
        MSTypes.VAR, MSTypes.FUNCTION, MSTypes.RETURN, MSTypes.IF, MSTypes.ELSE,
        MSTypes.FOR, MSTypes.WHILE, MSTypes.DO, MSTypes.BREAK, MSTypes.CONTINUE,
        MSTypes.TRY, MSTypes.CATCH, MSTypes.FINALLY, MSTypes.THROW,
        MSTypes.IMPORT, MSTypes.EXPORT, MSTypes.TRUE, MSTypes.FALSE,
        MSTypes.NULL, MSTypes.UNDEFINED
    );
    
    @Override
    public @NotNull Lexer createLexer(Project project) {
        return new MagicScriptLexerAdapter();
    }
    
    @Override
    public @NotNull PsiParser createParser(Project project) {
        return new MagicScriptParser();
    }
    
    @Override
    public @NotNull IFileElementType getFileNodeType() {
        return FILE;
    }
    
    @Override
    public @NotNull TokenSet getWhitespaceTokens() {
        return WHITE_SPACES;
    }
    
    @Override
    public @NotNull TokenSet getCommentTokens() {
        return COMMENTS;
    }
    
    @Override
    public @NotNull TokenSet getStringLiteralElements() {
        return STRINGS;
    }
    
    @Override
    public @NotNull PsiElement createElement(ASTNode node) {
        return MSTypes.Factory.createElement(node);
    }
    
    @Override
    public @NotNull PsiFile createFile(@NotNull FileViewProvider viewProvider) {
        return new MSFile(viewProvider);
    }
}