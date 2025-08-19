package com.magicapi.idea.formatting;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.magicapi.idea.lang.MagicScriptLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Magic Script代码格式化模型构建器
 */
public class MagicScriptFormattingModelBuilder implements FormattingModelBuilder {
    
    @Override
    @NotNull
    public FormattingModel createModel(@NotNull FormattingContext formattingContext) {
        PsiElement element = formattingContext.getPsiElement();
        CodeStyleSettings settings = formattingContext.getCodeStyleSettings();
        
        MagicScriptBlock rootBlock = new MagicScriptBlock(element.getNode(), null, null, settings);
        return FormattingModelProvider.createFormattingModelForPsiFile(
            element.getContainingFile(),
            rootBlock,
            settings
        );
    }
    
    @Override
    @Nullable
    public TextRange getRangeAffectingIndent(@NotNull PsiFile file, int offset, @NotNull ASTNode elementAtOffset) {
        return null;
    }
}