package com.magicapi.idea.navigation;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.magicapi.idea.lang.psi.MSTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Magic Script Goto声明处理器
 * 实现Ctrl+点击跳转到定义功能
 */
public class MagicScriptGotoDeclarationHandler implements GotoDeclarationHandler {
    
    @Override
    @Nullable
    public PsiElement[] getGotoDeclarationTargets(@Nullable PsiElement sourceElement, 
                                                 int offset, 
                                                 Editor editor) {
        
        if (sourceElement == null) {
            return null;
        }
        
        // 检查是否为Magic Script标识符
        if (sourceElement.getNode().getElementType() != MSTypes.IDENTIFIER) {
            return null;
        }
        
        // 获取引用并解析
        PsiReference reference = sourceElement.getReference();
        if (reference != null) {
            PsiElement target = reference.resolve();
            if (target != null) {
                return new PsiElement[]{target};
            }
        }
        
        // 如果没有直接引用，尝试查找所有可能的引用
        PsiReference[] references = sourceElement.getReferences();
        for (PsiReference ref : references) {
            PsiElement target = ref.resolve();
            if (target != null) {
                return new PsiElement[]{target};
            }
        }
        
        return null;
    }
}