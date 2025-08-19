package com.magicapi.idea.refactoring;

import com.intellij.lang.refactoring.RefactoringSupportProvider;
import com.intellij.psi.PsiElement;
import com.magicapi.idea.lang.psi.MSFunctionDeclaration;
import com.magicapi.idea.lang.psi.MSVarDeclaration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Magic Script 重构支持提供器
 */
public class MagicScriptRefactoringSupportProvider extends RefactoringSupportProvider {
    
    @Override
    public boolean isSafeDeleteAvailable(@NotNull PsiElement element) {
        return element instanceof MSFunctionDeclaration || element instanceof MSVarDeclaration;
    }
    
    @Override
    public boolean isInplaceRenameAvailable(@NotNull PsiElement element, PsiElement context) {
        return element instanceof MSFunctionDeclaration || element instanceof MSVarDeclaration;
    }
    
    @Override
    public boolean isMemberInplaceRenameAvailable(@NotNull PsiElement element, @Nullable PsiElement context) {
        return isInplaceRenameAvailable(element, context);
    }
}