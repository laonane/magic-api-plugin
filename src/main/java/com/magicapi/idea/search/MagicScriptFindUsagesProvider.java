package com.magicapi.idea.search;

import com.intellij.lang.cacheBuilder.DefaultWordsScanner;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.TokenSet;
import com.magicapi.idea.lang.lexer.MagicScriptLexer;
import com.magicapi.idea.lang.psi.MSFunctionDeclaration;
import com.magicapi.idea.lang.psi.MSTypes;
import com.magicapi.idea.lang.psi.MSVarDeclaration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Magic Script 查找使用处提供器
 */
public class MagicScriptFindUsagesProvider implements FindUsagesProvider {
    
    @Override
    @Nullable
    public WordsScanner getWordsScanner() {
        return null; // 简化实现，避免复杂的词法分析器转换
    }
    
    @Override
    public boolean canFindUsagesFor(@NotNull PsiElement psiElement) {
        return psiElement instanceof MSFunctionDeclaration ||
               psiElement instanceof MSVarDeclaration;
    }
    
    @Override
    @NotNull
    public String getHelpId(@NotNull PsiElement psiElement) {
        return "Magic Script Find Usages";
    }
    
    @Override
    @NotNull
    public String getType(@NotNull PsiElement element) {
        if (element instanceof MSFunctionDeclaration) {
            return "function";
        } else if (element instanceof MSVarDeclaration) {
            return "variable";
        }
        return "";
    }
    
    @Override
    @NotNull
    public String getDescriptiveName(@NotNull PsiElement element) {
        if (element instanceof MSFunctionDeclaration) {
            String name = ((MSFunctionDeclaration) element).getName();
            return name != null ? name : "<unnamed>";
        } else if (element instanceof MSVarDeclaration) {
            String name = ((MSVarDeclaration) element).getName();
            return name != null ? name : "<unnamed>";
        }
        return "";
    }
    
    @Override
    @NotNull
    public String getNodeText(@NotNull PsiElement element, boolean useFullName) {
        return getDescriptiveName(element);
    }
}