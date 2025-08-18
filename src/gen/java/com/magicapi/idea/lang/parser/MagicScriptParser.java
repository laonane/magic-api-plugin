package com.magicapi.idea.lang.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

public class MagicScriptParser implements PsiParser {
    
    @NotNull
    @Override
    public com.intellij.lang.ASTNode parse(@NotNull IElementType root, @NotNull PsiBuilder builder) {
        PsiBuilder.Marker rootMarker = builder.mark();
        
        // 简单的解析逻辑 - 暂时跳过所有token
        while (!builder.eof()) {
            builder.advanceLexer();
        }
        
        rootMarker.done(root);
        return builder.getTreeBuilt();
    }
}